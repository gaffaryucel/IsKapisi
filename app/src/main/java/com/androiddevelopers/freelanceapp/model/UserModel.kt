package com.androiddevelopers.freelanceapp.model

class UserModel{

    var userId: String? = null
    var username: String? = null
    var email: String? = null

    constructor()

    constructor(
        userId: String?,
        username: String?,
        email: String?,
    ) {
        this.userId = userId
        this.username = username
        this.email = email
    }
}

/*
 //Kulanabileceğimiz diğer elemanlar
   var fullName: String? = null
   var profileImageUrl: String? = null
   var skills: List<String>? = null
   var portfolio: List<PortfolioItem>? = null
   var reviews: List<Review>? = null
   var hourlyRate: Double? = null
   var availability: List<Availability>? = null
   var location: Location? = null
   var education: List<Education>? = null
   var certifications: List<Certification>? = null
   var languages: List<Language>? = null
   var projects: List<Project>? = null
   var workExperience: List<WorkExperience>? = null
   var socialMediaLinks: List<SocialMediaLink>? = null
   var contactInformation: ContactInformation? = null
   var paymentMethods: List<PaymentMethod>? = null
   var linkedAccounts: LinkedAccounts? = null

   fullName: String?,
   profileImageUrl: String?,
   skills: List<String>?,
   portfolio: List<PortfolioItem>?,
   reviews: List<Review>?,
   hourlyRate: Double?,
   availability: List<Availability>?,
   location: Location?,
   education: List<Education>?,
   certifications: List<Certification>?,
   languages: List<Language>?,
   projects: List<Project>?,
   workExperience: List<WorkExperience>?,
   socialMediaLinks: List<SocialMediaLink>?,
   contactInformation: ContactInformation?,
   paymentMethods: List<PaymentMethod>?,
   linkedAccounts: LinkedAccounts?

   this.fullName = fullName
   this.profileImageUrl = profileImageUrl
   this.skills = skills
   this.portfolio = portfolio
   this.reviews = reviews
   this.hourlyRate = hourlyRate
   this.availability = availability
   this.location = location
   this.education = education
   this.certifications = certifications
   this.languages = languages
   this.projects = projects
   this.workExperience = workExperience
   this.socialMediaLinks = socialMediaLinks
   this.contactInformation = contactInformation
   this.paymentMethods = paymentMethods
   this.linkedAccounts = linkedAccounts
  */