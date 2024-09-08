package com.universitinder.app.controllers

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.universitinder.app.models.FAQ
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class FaqController {
    private val firestore = Firebase.firestore

    suspend fun getFAQ(email: String, documentID: String) : FAQ? {
        val response = CompletableDeferred<FAQ?>()

        coroutineScope {
            launch(Dispatchers.IO) {
                val faqRef = firestore.collection("users").document(email).collection("school").document("school").collection("FAQs").document(documentID)
                faqRef.get()
                    .addOnSuccessListener { response.complete(it.toObject(FAQ::class.java)) }
                    .addOnFailureListener { response.complete(null) }
            }
        }

        return response.await()
    }

    suspend fun getFAQs(email: String) : List<DocumentSnapshot> {
        val response = CompletableDeferred<List<DocumentSnapshot>>(null)

        coroutineScope {
            launch(Dispatchers.IO) {
                val faqsRef = firestore.collection("users").document(email).collection("school").document("school").collection("FAQs")
                faqsRef.get()
                    .addOnSuccessListener { response.complete(it.documents) }
                    .addOnFailureListener { response.complete(listOf()) }
            }
        }

        return response.await()
    }

    suspend fun createFAQ(email: String, faq: FAQ) : Boolean {
        val response = CompletableDeferred<Boolean>(null)

        coroutineScope {
            launch(Dispatchers.IO) {
                val faqsRef = firestore.collection("users").document(email).collection("school").document("school").collection("FAQs")
                faqsRef.document()
                    .set(faq)
                    .addOnSuccessListener { response.complete(true) }
                    .addOnFailureListener { response.complete(false) }
            }
        }

        return response.await()
    }

    suspend fun updateFAQ(email: String, documentID: String, faq: FAQ) : Boolean {
        val response = CompletableDeferred<Boolean>(null)

        coroutineScope {
            launch(Dispatchers.IO) {
                val faqsRef = firestore.collection("users").document(email).collection("school").document("school").collection("FAQs")
                faqsRef.document(documentID)
                    .update("question", faq.question, "answer", faq.answer)
                    .addOnSuccessListener { response.complete(true) }
                    .addOnFailureListener { response.complete(false) }
            }
        }

        return response.await()
    }

    suspend fun createFAQs(email: String, faqs: List<FAQ>) : Boolean {
        val response = CompletableDeferred<Boolean>(null)

        coroutineScope {
            launch(Dispatchers.IO) {
                val faqsRef = firestore.collection("users").document(email).collection("school").document("school").collection("FAQs")
                val batch = firestore.batch()

                async {
                    faqs.forEachIndexed { index, faq ->
                        val faqDocumentRef = faqsRef.document(index.toString())
                        batch.set(faqDocumentRef, faq)
                    }
                }.await()

                async {
                    batch.commit()
                        .addOnSuccessListener { response.complete(true) }
                        .addOnFailureListener { response.complete(false) }
                }.await()
            }
        }

        return response.await()
    }
}