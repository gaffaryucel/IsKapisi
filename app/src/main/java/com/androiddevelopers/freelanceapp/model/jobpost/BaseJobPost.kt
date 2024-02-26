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
    var ownerToken: String? = null // yapılması gereken işler

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
        worksToBeDone: List<String>?,
        ownerToken: String?
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
        this.ownerToken = ownerToken
    }

    override fun equals(other: Any?): Boolean {
        return this === other &&
                this.title == other.title &&
                this.description == other.description &&
                this.images?.toTypedArray().contentEquals(
                    other.images?.toTypedArray()
                ) &&
                this.skillsRequired?.toTypedArray().contentEquals(
                    other.skillsRequired?.toTypedArray()
                ) &&
                this.savedUsers?.toTypedArray().contentEquals(
                    other.savedUsers?.toTypedArray()
                ) &&
                this.budget == other.budget &&
                this.deadline == other.deadline &&
                this.location == other.location &&
                this.datePosted == other.datePosted &&
                this.applicants?.toTypedArray().contentEquals(
                    other.applicants?.toTypedArray()
                ) &&
                this.status == other.status &&
                this.additionalDetails == other.additionalDetails &&
                this.savedUsers?.toTypedArray().contentEquals(
                    other.savedUsers?.toTypedArray()
                ) &&
                this.viewCount?.toTypedArray().contentEquals(
                    other.viewCount?.toTypedArray()
                ) &&
                this.isUrgent == other.isUrgent &&
                this.worksToBeDone?.toTypedArray().contentEquals(
                    other.worksToBeDone?.toTypedArray()
                )
    }

    override fun hashCode(): Int {
        var result = postId?.hashCode() ?: 0
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (images?.hashCode() ?: 0)
        result = 31 * result + (skillsRequired?.hashCode() ?: 0)
        result = 31 * result + (budget?.hashCode() ?: 0)
        result = 31 * result + (deadline?.hashCode() ?: 0)
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + (datePosted?.hashCode() ?: 0)
        result = 31 * result + (applicants?.hashCode() ?: 0)
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + (additionalDetails?.hashCode() ?: 0)
        result = 31 * result + (savedUsers?.hashCode() ?: 0)
        result = 31 * result + (viewCount?.hashCode() ?: 0)
        result = 31 * result + (isUrgent?.hashCode() ?: 0)
        result = 31 * result + (worksToBeDone?.hashCode() ?: 0)
        result = 31 * result + (ownerToken?.hashCode() ?: 0)
        return result
    }
}