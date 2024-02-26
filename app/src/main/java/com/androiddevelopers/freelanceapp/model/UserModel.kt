package com.androiddevelopers.freelanceapp.model

import com.androiddevelopers.freelanceapp.util.UserStatus

class UserModel {

    var userId: String? = null
    var username: String? = null
    var email: String? = null
    var profileImageUrl: String? = null
    var fullName: String? = null
    var bio: String? = null
    var phone: String? = null
    var jobTitle: String? = null
    var jobDescription: String? = null
    var skills: List<String>? = null
    var portfolio: List<PortfolioItem>? = null
    var reviews: List<Review>? = null
    var availability: List<Availability>? = null
    var location: Location? = null
    var education: List<Education>? = null
    var certifications: List<Certification>? = null
    var languages: List<Language>? = null
    var workExperience: List<WorkExperience>? = null
    var socialMediaLinks: List<SocialMediaLink>? = null
    var contactInformation: ContactInformation? = null
    var paymentMethods: List<PaymentMethod>? = null
    var userType: UserStatus? = null
    var completedJobs: Int? = null // Tamamlanan iş sayısı
    var canceledJobs: Int? = null // İptal edilen iş sayısı
    var unfinishedJobs: Int? = null // Zamanında bitmeyen iş sayısı
    var rating: Double? = null
    var token: String? = null

    constructor()

    constructor(
        userId: String? = null,
        username: String? = null,
        email: String? = null,
        profileImageUrl: String? = null,
        fullName: String? = null,
        bio: String? = null,
        phone: String? = null,
        jobTitle: String? = null,
        jobDescription: String? = null,
        skills: List<String>? = null,
        portfolio: List<PortfolioItem>? = null,
        reviews: List<Review>? = null,
        availability: List<Availability>? = null,
        location: Location? = null,
        education: List<Education>? = null,
        certifications: List<Certification>? = null,
        languages: List<Language>? = null,
        workExperience: List<WorkExperience>? = null,
        socialMediaLinks: List<SocialMediaLink>? = null,
        contactInformation: ContactInformation? = null,
        paymentMethods: List<PaymentMethod>? = null,
        userType: UserStatus? = null,
        completedJobs: Int? = null,
        canceledJobs: Int? = null,
        unfinishedJobs: Int? = null,
        rating: Double? = null,
        token: String? = null
    ) {
        this.userId = userId
        this.username = username
        this.email = email
        this.profileImageUrl = profileImageUrl
        this.fullName = fullName
        this.bio = bio
        this.phone = phone
        this.jobTitle = jobTitle
        this.jobDescription = jobDescription
        this.skills = skills
        this.portfolio = portfolio
        this.reviews = reviews
        this.availability = availability
        this.location = location
        this.education = education
        this.certifications = certifications
        this.languages = languages
        this.workExperience = workExperience
        this.socialMediaLinks = socialMediaLinks
        this.contactInformation = contactInformation
        this.paymentMethods = paymentMethods
        this.userType = userType
        this.completedJobs = completedJobs
        this.canceledJobs = canceledJobs
        this.unfinishedJobs = unfinishedJobs
        this.rating = rating
        this.token = token
    }

    override fun equals(other: Any?): Boolean {
        return this === other &&
                this.username == other.username &&
                this.email == other.email &&
                this.profileImageUrl == other.profileImageUrl &&
                this.fullName == other.fullName &&
                this.bio == other.bio &&
                this.jobTitle == other.jobTitle &&
                this.jobDescription == other.jobDescription &&
                this.skills?.toTypedArray().contentEquals(
                    other.skills?.toTypedArray()
                )
    }

    override fun hashCode(): Int {
        var result = userId?.hashCode() ?: 0
        result = 31 * result + (username?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (profileImageUrl?.hashCode() ?: 0)
        result = 31 * result + (fullName?.hashCode() ?: 0)
        result = 31 * result + (bio?.hashCode() ?: 0)
        result = 31 * result + (phone?.hashCode() ?: 0)
        result = 31 * result + (jobTitle?.hashCode() ?: 0)
        result = 31 * result + (jobDescription?.hashCode() ?: 0)
        result = 31 * result + (skills?.hashCode() ?: 0)
        result = 31 * result + (portfolio?.hashCode() ?: 0)
        result = 31 * result + (reviews?.hashCode() ?: 0)
        result = 31 * result + (availability?.hashCode() ?: 0)
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + (education?.hashCode() ?: 0)
        result = 31 * result + (certifications?.hashCode() ?: 0)
        result = 31 * result + (languages?.hashCode() ?: 0)
        result = 31 * result + (workExperience?.hashCode() ?: 0)
        result = 31 * result + (socialMediaLinks?.hashCode() ?: 0)
        result = 31 * result + (contactInformation?.hashCode() ?: 0)
        result = 31 * result + (paymentMethods?.hashCode() ?: 0)
        result = 31 * result + (userType?.hashCode() ?: 0)
        result = 31 * result + (completedJobs ?: 0)
        result = 31 * result + (canceledJobs ?: 0)
        result = 31 * result + (unfinishedJobs ?: 0)
        result = 31 * result + (rating?.hashCode() ?: 0)
        result = 31 * result + (token?.hashCode() ?: 0)
        return result
    }
}

data class PortfolioItem(
    var title: String? = null,
    var description: String? = null,
    var imageUrl: String? = null
)

data class Review(
    var reviewerName: String? = null,
    var comment: String? = null,
    var rating: Double? = null
)

data class Availability(
    var dayOfWeek: String? = null,
    var startTime: String? = null,
    var endTime: String? = null
)

data class Location(
    var country: String? = null,
    var city: String? = null,
    var address: String? = null
)

data class Education(
    var institution: String? = null,
    var degree: String? = null,
    var graduationYear: Int? = null
)

data class Certification(
    var name: String? = null,
    var issuingOrganization: String? = null,
    var issuanceDate: String? = null
)

data class Language(
    var language: String? = null,
    var proficiency: String? = null
)

data class WorkExperience(
    var companyName: String? = null,
    var position: String? = null,
    var startDate: String? = null,
    var endDate: String? = null
)

data class SocialMediaLink(
    var platform: String? = null,
    var url: String? = null
)

data class ContactInformation(
    var email: String? = null,
    var phone: String? = null
)

data class PaymentMethod(
    var method: String? = null,
    var accountDetails: String? = null
)


