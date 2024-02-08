package com.androiddevelopers.freelanceapp.model.jobpost

import com.androiddevelopers.freelanceapp.util.JobStatus

class FreelancerJobPost: BaseJobPost {
    var freelancerId: String? = null // İlanı oluşturan freelancer'ın kimliği
    var rating: Double? = null // İşveren tarafından işin başarı derecesini belirtmek için
    var unfinishedJobs: Int? = null // Zamanında bitmeyen iş sayısı

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
        unfinishedJobs: Int?,
        viewCount: List<String>?,
        isUrgent: Boolean?,
        freelancerId: String?,
        rating: Double?
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
        this.freelancerId = freelancerId
        this.rating = rating
        this.unfinishedJobs = unfinishedJobs
    }
}
