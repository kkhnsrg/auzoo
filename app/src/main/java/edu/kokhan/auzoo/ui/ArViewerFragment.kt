package edu.kokhan.auzoo.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.filament.gltfio.Animator
import com.google.android.filament.gltfio.FilamentAsset
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import durdinapps.rxfirebase2.RxFirebaseStorage
import edu.kokhan.auzoo.R
import edu.kokhan.auzoo.database.FirestoreDatabase
import edu.kokhan.auzoo.database.FirestoreDatabase.Companion.getRef
import edu.kokhan.auzoo.utils.getPhotoGalleryPath
import edu.kokhan.auzoo.utils.takePhotoFromCamera
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_ar_viewer.*
import org.koin.android.ext.android.inject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ArViewerFragment : Fragment() {

    private val args: ArViewerFragmentArgs by navArgs()
    private val firestore: FirestoreDatabase by inject()

    private lateinit var arFragment: ArFragment

    private class AnimationInstance internal constructor(
        var animator: Animator,
        index: Int,
        var startTime: Long
    ) {
        var duration: Float
        var index: Int

        init {
            duration = animator.getAnimationDuration(index)
            this.index = index
        }
    }

    private val animators: MutableSet<AnimationInstance> = mutableSetOf()

    private val colors =
        listOf(
            Color(0f, 0f, 0f, 1f),
            Color(1f, 0f, 0f, 1f),
            Color(0f, 1f, 0f, 1f),
            Color(0f, 0f, 1f, 1f),
            Color(1f, 1f, 0f, 1f),
            Color(0f, 1f, 1f, 1f),
            Color(1f, 0f, 1f, 1f),
            Color(1f, 1f, 1f, 1f)
        )
    private var nextColor = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ar_viewer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        arFragment =
            this.childFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        firestore.getOrDownloadModel(args.animalName)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                showProgress()
            }
            .doFinally { hideProgress() }
            .subscribe({ file ->
                Log.d("AR Present", "onLoad: ${file.path}")
                floatingPlaceModelButton.setOnClickListener {
                    Log.d("AR Present", "Click")
                    addObject(
                        Uri.parse(file.path)
                    )
                }
                floatingPhotoButton.setOnClickListener {
                    takePhoto()
                }
            }, {
                Log.d("AR Present", "onError $it")
            })

        arFragment =
            this.childFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        super.onActivityCreated(savedInstanceState)
    }

    private fun takePhoto() {
        val sceneView = arFragment.arSceneView

        takePhotoFromCamera(sceneView)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                Toast.makeText(
                context,
                "Photo saved on your device!",
                Toast.LENGTH_SHORT
            ).show() }
            .doOnError {
                Toast.makeText(
                context,
                "Photo error: ${it.message}",
                Toast.LENGTH_SHORT
            ).show() }
            .subscribe({
                Log.e("ArViewerFragment", "photoSaved ${it.path}")
            }, {
                Log.e("ArViewerFragment", "onError $it")
            })
    }

    private fun hideProgress() {
        requireActivity().runOnUiThread { progress.visibility = View.GONE }
    }

    private fun showProgress() {
        requireActivity().runOnUiThread { progress.visibility = View.VISIBLE }
    }

    // Simply returns the center of the screen
    private fun getScreenCenter(): Point {
        val view = requireView().rootView
        return Point(view.width / 2, view.height / 2)
    }

    private fun addObject(model: Uri) {
        val frame = arFragment.arSceneView.arFrame
        val point = getScreenCenter()
        if (frame != null) {
            val hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    placeObject(arFragment, hit.createAnchor(), model)
                    break
                }
            }
        }
    }

    private fun placeObject(fragment: ArFragment, anchor: Anchor, model: Uri) {

        ModelRenderable.builder()
            .setSource(
                context,
                model
            )
            .setIsFilamentGltf(true)
            .build()
            .thenAccept {
                addNodeToScene(fragment, anchor, it)
            }
            .exceptionally {
                Log.d("Placing exception", it.toString())
                Toast.makeText(
                    context,
                    "Could not fetch model from $model",
                    Toast.LENGTH_SHORT
                ).show()
                return@exceptionally null
            }
    }

    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderable: ModelRenderable) {

        val anchorNode = AnchorNode(anchor)

        // TransformableNode means the user to move, scale and rotate the model
        val transformableNode = TransformableNode(fragment.transformationSystem)
        transformableNode.renderable = renderable
        transformableNode.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()

        val filamentAsset: FilamentAsset = transformableNode.renderableInstance!!.filamentAsset!!
        if (filamentAsset.animator.animationCount > 0) {
            animators.add(
                AnimationInstance(
                    filamentAsset.animator,
                    0,
                    System.nanoTime()
                )
            )
        }

        val color: Color = colors[nextColor]
        nextColor++
        for (i in 0 until renderable.submeshCount) {
            val material = renderable.getMaterial(i)
            material.setFloat4("baseColorFactor", color)
        }

        fragment
            .arSceneView
            .scene
            .addOnUpdateListener {
                val time = System.nanoTime()
                for (animator: AnimationInstance in animators) {
                    animator.animator.applyAnimation(
                        animator.index,
                        ((time - animator.startTime) / TimeUnit.SECONDS.toNanos(1).toFloat()) % animator.duration
                    )
                    animator.animator.updateBoneMatrices()
                }
            }
    }
}
