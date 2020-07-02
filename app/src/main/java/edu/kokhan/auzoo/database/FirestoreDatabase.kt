package edu.kokhan.auzoo.database

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import durdinapps.rxfirebase2.RxFirebaseStorage
import durdinapps.rxfirebase2.RxFirestore
import edu.kokhan.auzoo.model.Animal
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File

class FirestoreDatabase(val context: Context) {

    private val database = FirebaseFirestore.getInstance()
    private val firestore = FirebaseStorage.getInstance()

    companion object {
        private const val ANIMALS_TABLE = "animals"
        private const val ANIMALS_STORAGE = "animals_animated"
        private const val MODEL_FILE_FORMAT = ".glb"
        private const val MODEL_FILE_NAME = "model$MODEL_FILE_FORMAT"
        fun nameToFolderName(name: String) = name.replace(" ", "")
        fun getRef(path: String) = "$ANIMALS_STORAGE/${nameToFolderName(
            path
        )}/$MODEL_FILE_NAME"
    }

    fun getAllAnimals(): Single<List<Animal>> =
        RxFirestore.getCollection(database.collection(ANIMALS_TABLE))
            .map {
                it.documents.map { document ->
                    Animal(
                        document.data!!["name"].toString(),
                        document.data!!["lifespan"].toString(),
                        document.data!!["length"].toString(),
                        document.data!!["height"].toString(),
                        document.data!!["mass"].toString(),
                        document.data!!["description"].toString(),
                        document.data!!["short_description"].toString(),
                        document.data!!["photoUrl"].toString()
                    )
                }
            }
            .map { it.toList() }
            .toSingle(emptyList())
            .subscribeOn(Schedulers.io())

    fun getAnimalByName(name: String): Single<Animal> =
        RxFirestore.getDocument(database.collection(ANIMALS_TABLE).document(
            nameToFolderName(
                name
            )
        ))
            .map { document ->
                Animal(
                    document.data!!["name"].toString(),
                    document.data!!["lifespan"].toString(),
                    document.data!!["length"].toString(),
                    document.data!!["height"].toString(),
                    document.data!!["mass"].toString(),
                    document.data!!["description"].toString(),
                    document.data!!["short_description"].toString(),
                    document.data!!["photoUrl"].toString()
                )
            }
            .toSingle()
            .subscribeOn(Schedulers.io())

    fun getOrDownloadModel(animalName: String): Single<File> {
        val dir = File("${context.getExternalFilesDir(null)}/${nameToFolderName(
            animalName
        )}")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val modelFile = File(dir,
            MODEL_FILE_NAME
        )
        if (modelFile.exists() && modelFile.isFile) {
            Log.d("FirestoreDatabase", "image file exists")
            return Single.just(modelFile)
        }
        Log.d("FirestoreDatabase", "try to download file ${modelFile.absolutePath}")

        Log.d("FirestoreDatabase",
            getRef(
                animalName
            )
        )
        val ref = firestore.getReference(
            getRef(
                animalName
            )
        )
        return RxFirebaseStorage.getFile(ref, modelFile)
            .map { modelFile }
            .doOnSuccess { Log.d("FirestoreDatabase", "OnSuccess: ${it.path}") }
            .doOnError { Log.e("FirestoreDatabase", "OnError $it") }
            .subscribeOn(Schedulers.io())
    }
}