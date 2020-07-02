package edu.kokhan.auzoo.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.kokhan.auzoo.database.FirestoreDatabase
import kotlinx.coroutines.*

class SplashViewModel(
    firestore: FirestoreDatabase //for connection check
) : ViewModel() {

    val shouldShowPresentationData = MutableLiveData<Boolean>()
    val rotateImageData = MutableLiveData<Unit>()

    private val job = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    init {
        uiScope.launch {

            // in this we should add check api to download models

            delay(3000)

            shouldShowPresentationData.postValue(false)
        }

        uiScope.launch {
            delay(1000)

            rotateImageData.postValue(Unit)
        }
    }

    override fun onCleared() {
        job.cancelChildren()
    }
}