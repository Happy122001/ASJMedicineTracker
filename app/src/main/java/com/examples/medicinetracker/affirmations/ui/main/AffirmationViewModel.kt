package com.examples.medicinetracker.affirmations.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examples.medicinetracker.affirmations.network.Affirmation
import com.examples.medicinetracker.affirmations.network.AffirmationApi
import com.examples.medicinetracker.affirmations.network.AffirmationImage
import com.examples.medicinetracker.affirmations.network.AffirmationImageApi
import kotlinx.coroutines.launch

enum class AffirmationImageApiStatus {LOADING, ERROR, DONE}
enum class AffirmationApiStatus {LOADING, ERROR, DONE}
enum class Status {LOADING, ERROR, DONE}
private const val TAG = "Affirmations"

class AffirmationViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val _affirmationStatus = MutableLiveData<AffirmationApiStatus>()
    val affirmationStatus: LiveData<AffirmationApiStatus> = _affirmationStatus

    private val _affirmationImageStatus = MutableLiveData<AffirmationImageApiStatus>()
    val affirmationImageStatus: LiveData<AffirmationImageApiStatus> = _affirmationImageStatus

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    private val _affirmations = MutableLiveData<List<Affirmation>>()
    val affirmations: LiveData<List<Affirmation>> = _affirmations

    private val _affirmationImages = MutableLiveData<List<AffirmationImage>>()
    val affirmationImages: LiveData<List<AffirmationImage>> = _affirmationImages

    init {
        _status.value = Status.LOADING
        getAffirmations()
        getAffirmationImages()
    }

    private fun getAffirmations() {

        viewModelScope.launch {
            _affirmationStatus.value = AffirmationApiStatus.LOADING
            try {
                _affirmations.value = AffirmationApi.retrofitService.getQuotes()
                _affirmationStatus.value = AffirmationApiStatus.DONE
                setStatus()
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
                _affirmationStatus.value = AffirmationApiStatus.ERROR
                _affirmations.value = listOf()
                setStatus()
            }
        }
    }

    private fun getAffirmationImages() {

        viewModelScope.launch {
            _affirmationImageStatus.value = AffirmationImageApiStatus.LOADING
            try {
                _affirmationImages.value = AffirmationImageApi.retrofitService.getPhotos()
                _affirmationImageStatus.value = AffirmationImageApiStatus.DONE
                setStatus()
            } catch (e: Exception) {
                _affirmationImageStatus.value = AffirmationImageApiStatus.ERROR
                _affirmationImages.value = listOf()
                setStatus()
            }
        }
    }

    private fun setStatus() {
        if (_affirmationStatus.value == AffirmationApiStatus.DONE &&
            _affirmationImageStatus.value == AffirmationImageApiStatus.DONE) {
            _status.value = Status.DONE
        }else if(_affirmationStatus.value == AffirmationApiStatus.ERROR ||
            _affirmationImageStatus.value == AffirmationImageApiStatus.ERROR) {
            _status.value = Status.ERROR
        }else{
            _status.value = Status.LOADING
        }
    }
}