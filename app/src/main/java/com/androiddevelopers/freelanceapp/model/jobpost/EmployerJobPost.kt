package com.androiddevelopers.freelanceapp.model.jobpost

import com.androiddevelopers.freelanceapp.util.JobStatus

class EmployerJobPost : BaseJobPost {
    var employerId: String? = null // İlanı oluşturan işverenin kimliği
    var aboutYou: List<String>? = null // Freelancerden istenen özellikler

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
        employerId: String? = null,
        worksToBeDone: List<String>? = null,
        ownerToken: String? = null,
        aboutYou: List<String>? = null
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
        this.employerId = employerId
        this.aboutYou = aboutYou
    }
}
