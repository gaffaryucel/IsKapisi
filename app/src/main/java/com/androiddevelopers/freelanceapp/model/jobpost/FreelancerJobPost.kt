package com.androiddevelopers.freelanceapp.model.jobpost

import com.androiddevelopers.freelanceapp.util.JobStatus

class FreelancerJobPost : BaseJobPost {
    var freelancerId: String? = null // İlanı oluşturan freelancer'ın kimliği
    var rating: Double? = null // İşveren tarafından işin başarı derecesini belirtmek için
    var likes: List<String>? = null //gönderiyi beğenen kişiler

    constructor() : super()

    constructor(
        postId: String? = null,
        title: String? = null,
        description: String? = null,
        images: List<String>? = null,
        skillsRequired: List<String>? = null,
        budget: Double? = null,
        deadline: String? = null,
        location: String? = null,
        datePosted: String? = null,
        applicants: List<String>? = null,
        status: JobStatus? = null,
        additionalDetails: String? = null,
        savedUsers: List<String>? = null,
        viewCount: List<String>? = null,
        isUrgent: Boolean? = null,
        freelancerId: String? = null,
        rating: Double? = null,
        likes: List<String>? = null,
        worksToBeDone: List<String>? = null,
        ownerToken: String? = null
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
        savedUsers,
        viewCount,
        isUrgent,
        worksToBeDone,
        ownerToken
    ) {
        this.freelancerId = freelancerId
        this.rating = rating
        this.likes = likes
    }
}
