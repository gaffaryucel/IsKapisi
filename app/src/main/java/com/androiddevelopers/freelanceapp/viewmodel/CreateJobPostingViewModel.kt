package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateJobPostingViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace
) : ViewModel() {
    //TODO: ViewModel methods will be created
}