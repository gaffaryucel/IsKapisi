package com.androiddevelopers.freelanceapp.model.jobpost

import com.androiddevelopers.freelanceapp.util.JobStatus

class EmployerJobPost : BaseJobPost {
    var employerId: String? = null // İlanı oluşturan işverenin kimliği
    var canceledJobs: Int? = null // İptal edilen iş sayısı

    constructor() : super()

    constructor(
        postId: String?,
        title: String?,
        description: String?,
        images: List<String>?,
        skillsRequired: List<String>?,
        budget: Double?,
        deadline: String?,
        location: String?,
        datePosted: String?,
        applicants: List<String>?,
        status: JobStatus?,
        additionalDetails: String?,
        completedJobs: Int?,
        canceledJobs: Int?,
        viewCount: List<String>?,
        isUrgent: Boolean?,
        employerId: String?
    ) : super(
        postId,
        title,
        description,
        images,
        skillsRequired,
        budget,
        deadline,
        location,
        datePosted,
        applicants,
        status,
        additionalDetails,
        completedJobs,
        viewCount,
        isUrgent
    ) {
        this.employerId = employerId
        this.canceledJobs = canceledJobs
    }
}
