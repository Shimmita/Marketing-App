package com.shimitadouglas.marketcm.admin_check

import android.content.Context
import android.util.Log
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shimitadouglas.marketcm.mains.Registration.Companion.ComradeUser
import com.shimitadouglas.marketcm.modal_data_admin_fetch.DataAdmin
import com.shimitadouglas.marketcm.modal_data_profile.DataProfile

class AdministrationVerify(var context: Context) {
    companion object {
        private const val TAG = "AdministrationVerify"
        //
    }

    private val AdminCollection = "Admin"
    private val AdministrationDocument = "admin"
    private val UsersCollection = ComradeUser


    fun verifyIsAdminShowShield(menuShield: MenuItem) {
        //declaration of admin variables of comparisons
        var phoneAdmin = ""
        var passwordAdmin = ""
        var emailOneAdmin = ""
        var emailBAdmin = ""
        //declaration of user variables
        var userPhone = ""
        var userEmail = ""
        var userPassword = ""
        //get the menuItem passed through the class constructor and the make it visible if user is admin else no visible

        val storeAdmin = FirebaseFirestore.getInstance()
        storeAdmin.collection(AdminCollection).document(AdministrationDocument).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val classFilter: DataAdmin? = it.toObject(DataAdmin::class.java)
                    if (classFilter != null) {
                        phoneAdmin = classFilter.phone.toString()
                        passwordAdmin = classFilter.password.toString()
                        emailOneAdmin = classFilter.email.toString()
                        emailBAdmin = classFilter.emailB.toString()

                        //fetch the user data from the public repo (ComradeUsers/uID/data)
                        val uniqueUIDCurrentUser = FirebaseAuth.getInstance().currentUser?.uid
                        val storeUsers = FirebaseFirestore.getInstance()
                        if (uniqueUIDCurrentUser != null) {
                            storeUsers.collection(UsersCollection).document(uniqueUIDCurrentUser)
                                .get()
                                .addOnSuccessListener {

                                    if (it.exists()) {
                                        val classFilter: DataProfile? =
                                            it.toObject(DataProfile::class.java)
                                        if (classFilter != null) {
                                            userPhone = classFilter.PhoneNumber.toString()
                                            userEmail = classFilter.Email.toString()
                                            userPassword = classFilter.Password.toString()

                                            if (userPhone == phoneAdmin && userEmail == emailOneAdmin && userPassword == passwordAdmin) {
                                                //make the admin shield menu visible
                                                if (!menuShield.isVisible) {
                                                    menuShield.isVisible = true
                                                    Log.d(
                                                        TAG,
                                                        "verifyIsAdminShowShield: user is admin"
                                                    )
                                                }
                                                //
                                            } else if (userPhone == phoneAdmin && userEmail == emailBAdmin && userPassword == passwordAdmin) {

                                                if (!menuShield.isVisible) {
                                                    menuShield.isVisible = true
                                                    Log.d(
                                                        TAG,
                                                        "verifyIsAdminShowShield: user is admin"
                                                    )
                                                }
                                            } else {
                                                //no visible admin menu since user is not an admin
                                                menuShield.isVisible = false
                                                Log.d(
                                                    TAG,
                                                    "verifyIsAdminShowShield: user not admin"
                                                )
                                                //
                                            }
                                        }
                                    }

                                }
                        }
                        //
                    }
                }
            }

    }


}