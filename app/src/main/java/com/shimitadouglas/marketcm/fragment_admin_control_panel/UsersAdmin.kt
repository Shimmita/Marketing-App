package com.shimitadouglas.marketcm.fragment_admin_control_panel

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.adapter_all_users.AdapterAllUsers
import com.shimitadouglas.marketcm.mains.Registration.Companion.ComradeUser
import com.shimitadouglas.marketcm.modal_data_profile.DataProfile
import es.dmoral.toasty.Toasty

class UsersAdmin : Fragment() {
    //declaration of the globals
    lateinit var recyclerViewAllUsers: RecyclerView

    //
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //code begins
        var viewUsersAdmin = inflater.inflate(R.layout.users_admin, container, false)
        recyclerViewAllUsers = viewUsersAdmin.findViewById(R.id.rvUsersAll)
        //

        //fun to fetch all data of the users from the repository
        funLoadAllUsersFromRepository()
        //
        return viewUsersAdmin
        //code ends
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funLoadAllUsersFromRepository() {
        //code begins

        val sweetAlertDialog = SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.titleText = "Loading Users"
        sweetAlertDialog.create()
        sweetAlertDialog.show()

        val storeUsers = FirebaseFirestore.getInstance()
        storeUsers.collection(ComradeUser).get().addOnCompleteListener {
            if (it.isSuccessful) {
                //dismiss the sweet alert
                sweetAlertDialog.dismiss()

                val arrayListUsers = arrayListOf<DataProfile>()
                arrayListUsers.clear()
                val users = it.result
                for (user in users) {
                    val classFilter: DataProfile? = user.toObject(DataProfile::class.java)
                    if (classFilter != null) {

                        arrayListUsers.add(classFilter)
                    } else {
                        sweetAlertDialog.dismiss()
                        Toasty.error(requireActivity(), "something went wrong", Toasty.LENGTH_SHORT)
                            .show()
                    }
                }

                if (arrayListUsers.isNotEmpty()) {
                    val adapterUsers = AdapterAllUsers(requireActivity(), arrayListUsers)
                    recyclerViewAllUsers.apply {
                        adapter = adapterUsers
                        layoutManager = LinearLayoutManager(requireActivity())
                        adapterUsers.notifyDataSetChanged()
                    }

                } else {
                    sweetAlertDialog.dismiss()
                    Toasty.error(requireActivity(), "something went wrong", Toasty.LENGTH_SHORT)
                        .show()
                }

            } else if (!it.isSuccessful) {
                //dismiss dialog
                sweetAlertDialog.dismiss()
                //toast error occurred
                Toasty.error(requireActivity(), "something went wrong", Toasty.LENGTH_SHORT).show()
                return@addOnCompleteListener
            }
        }
        //code ends
    }
}