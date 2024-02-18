package com.androiddevelopers.freelanceapp.model.jobpost

import com.androiddevelopers.freelanceapp.util.JobStatus

open class BaseJobPost {
    var postId: String? = null // İlanın benzersiz kimliği
    var title: String? = null // İlan başlığı
    var description: String? = null // İlan açıklaması
    var images: List<String>? = null // İlan göreslleri
    var skillsRequired: List<String>? = null // İstenen yeteneklerin listesi
    var budget: Double? = null // İş için ayrılan bütçe
    var deadline: String? = null // İlanın son başvuru tarihi
    var location: String? = null // İşin yapılacağı konum
    var datePosted: String? = null // İlanın yayımlandığı tarih
    var applicants: List<String>? = null // Başvuranların listesi
    var status: JobStatus? = null // İlanın durumu (Açık, Kapalı, Tamamlanan)
    var additionalDetails: String? = null // İş ilanıyla ilgili ek detaylar
    var savedUsers: List<String>? = null // ilanı kaydeden kişiler
    var viewCount: List<String>? = null // İlanı görüntüleyen kişiler
    var isUrgent: Boolean? = null // İşin acil olduğunu belirtmek için
    var worksToBeDone: List<String>? = null // yapılması gereken işler

    constructor()
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
        savedUsers: List<String>?,
        viewCount: List<String>?,
        isUrgent: Boolean?,
        worksToBeDone: List<String>?
    ) {
        this.postId = postId
        this.title = title
        this.description = description
        this.images = images
        this.skillsRequired = skillsRequired
        this.budget = budget
        this.deadline = deadline
        this.location = location
        this.datePosted = datePosted
        this.applicants = applicants
        this.status = status
        this.additionalDetails = additionalDetails
        this.savedUsers = savedUsers
        this.viewCount = viewCount
        this.isUrgent = isUrgent
        this.worksToBeDone = worksToBeDone
    }

    override fun equals(other: Any?): Boolean {
        val newItem = other as BaseJobPost
        return if (this === newItem) {
            if (this.title != newItem.title) {
                if (this.description == newItem.description) {
                    if (
                        this.images?.toTypedArray().contentEquals(
                            other.images?.toTypedArray()
                        )
                    ) {
                        if (
                            this.skillsRequired?.toTypedArray().contentEquals(
                                other.skillsRequired?.toTypedArray()
                            )
                        ) {
                            if (
                                this.savedUsers?.toTypedArray().contentEquals(
                                    other.savedUsers?.toTypedArray()
                                )
                            ) {
                                true
                            } else {
                                false
                            }
                        } else {
                            false
                        }
                    } else {
                        false
                    }
                } else {
                    false
                }
            } else {
                false
            }
        } else {
            false
        }
    }
}