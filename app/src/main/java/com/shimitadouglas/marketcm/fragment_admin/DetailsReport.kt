package com.shimitadouglas.marketcm.fragment_admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.mains.Registration.Companion.ComradeUser
import com.shimitadouglas.marketcm.modal_data_posts.DataClassProductsData
import com.shimitadouglas.marketcm.modal_data_profile.DataProfile
import com.shimitadouglas.marketcm.modal_sheets.ModalPostProducts.Companion.CollectionAdminWarehouse
import com.shimitadouglas.marketcm.modal_sheets.ModalPostProducts.Companion.CollectionPost
import es.dmoral.toasty.Toasty

class DetailsReport(dataProductControlID: String?, dataVictimID: String?, dataSuspectID: String?) :
    Fragment() {
    //
    val productControlID = dataProductControlID
    val victimID = dataVictimID
    val suspectID = dataSuspectID
    //

    lateinit var imageViewCounterfeitProduct: ImageView
    lateinit var textViewCounterfeitProductTitle: TextView
    lateinit var textViewCounterfeitProductCode: TextView
    lateinit var textViewCounterfeitProductControllerID: TextView
    lateinit var textViewCounterfeitProductPlace: TextView
    lateinit var textViewCounterfeitProductPhone: TextView
    lateinit var imageViewCounterfeitVictim: ImageView
    lateinit var textViewCounterfeitVictimName: TextView
    lateinit var textViewCounterfeitVictimPhone: TextView
    lateinit var textViewCounterfeitVictimEmail: TextView
    lateinit var textViewCounterfeitVictimPlace: TextView
    lateinit var textViewCounterfeitVictimRegDate: TextView
    lateinit var imageViewSuspectCounterfeit: ImageView
    lateinit var textViewSuspectCounterfeitName: TextView
    lateinit var textViewSuspectCounterfeitPhone: TextView
    lateinit var textViewSuspectCounterfeitEmail: TextView
    lateinit var textViewSuspectCounterfeitPlace: TextView
    lateinit var textViewSuspectCounterfeitRegDate: TextView
    lateinit var textViewCounterfeitIsPhoneAltered: TextView
    lateinit var textViewCounterfeitIsPlaceAltered: TextView
    lateinit var textViewCounterfeitIsImageAltered: TextView
    lateinit var textViewPhoneAlterationStatement: TextView
    lateinit var textViewPlaceAlterationStatement: TextView
    lateinit var imageViewCounterfeitOriginalImage: ImageView
    lateinit var imageViewCounterfeitNewImage: ImageView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //init of the view
        val view: View = inflater.inflate(R.layout.layout_fragment_details, container, false)
        //
        //init values
        funInitGlobals(view)
        //
        //fun fetch product details
        funFetchProductDetails()
        //
        //fun fetch victim details
        funFetchVictimDetails()
        //
        //funFetchSuspectDetails
        funFetchSuspectDetails()
        //
        //return the view
        return view
        //
    }

    @SuppressLint("SetTextI18n")
    private fun funFetchSuspectDetails() {
        //code begins
        //fetch the suspect data from the store(Comrade/suspectID)
        val storeSuspect = FirebaseFirestore.getInstance()
        if (suspectID != null) {
            storeSuspect.collection(ComradeUser).document(suspectID).get().addOnSuccessListener {
                if (it.exists()) {
                    val classFilter: DataProfile? = it.toObject(DataProfile::class.java)
                    //using the glide library to load the suspect image
                    if (classFilter != null) {
                        Glide.with(requireActivity()).load(classFilter.ImagePath)
                            .into(imageViewSuspectCounterfeit)
                        //loading the other profile data
                        textViewSuspectCounterfeitRegDate.text = "registrationDate:\n" +
                                classFilter.registrationDate.toString()
                        textViewSuspectCounterfeitPlace.text =
                            "victim place:\n" + classFilter.University
                        textViewSuspectCounterfeitPhone.text =
                            "phone number:\n" + classFilter.PhoneNumber
                        textViewSuspectCounterfeitName.text =
                            "${classFilter.FirstName.toString()} ${classFilter.LastName.toString()}"
                        textViewSuspectCounterfeitEmail.text =
                            "email address:\n" + classFilter.Email
                        //
                    }

                }

            }.addOnFailureListener {
                funToastyFail("failed to load suspect's data")
            }
        }
        //code ends
    }

    @SuppressLint("SetTextI18n")
    private fun funFetchVictimDetails() {
        //code begins
        //fetch victim details from the repo ComradeUsers->(ComradeUsers/victimID)
        val storeVictim = FirebaseFirestore.getInstance()
        if (victimID != null) {
            storeVictim.collection(ComradeUser).document(victimID).get().addOnSuccessListener {
                if (it.exists()) {
                    val classFilter: DataProfile? = it.toObject(DataProfile::class.java)
                    //load the image of the victim using the glide library and other  profile details suspect
                    if (classFilter != null) {
                        //image load
                        Glide.with(requireActivity()).load(classFilter.ImagePath)
                            .into(imageViewCounterfeitVictim)
                        //
                        textViewCounterfeitVictimRegDate.text = "registrationDate:\n" +
                                classFilter.registrationDate.toString()
                        textViewCounterfeitVictimName.text =
                            "${classFilter.FirstName.toString()} ${classFilter.LastName.toString()}"
                        textViewCounterfeitVictimPhone.text =
                            "phone Number:\n" + classFilter.PhoneNumber.toString()
                        textViewCounterfeitVictimEmail.text =
                            "email address:\n" + classFilter.Email.toString()
                        textViewCounterfeitVictimPlace.text =
                            "place victim:\n" + classFilter.University.toString()

                    }
                    //
                }

            }.addOnFailureListener {
                //failed to load the data of the victim
                funToastyFail("failed to load victim's data")
                //
            }
        }
        //code ends
    }

    @SuppressLint("SetTextI18n")
    private fun funFetchProductDetails() {
        //code begins
        //fetch the data from the wareHouseAdmin store and then make relations to the public repo
        //(AdminWareHouse/productControlID)
        val storeAdminWareHouse = FirebaseFirestore.getInstance()
        if (productControlID != null) {
            storeAdminWareHouse.collection(CollectionAdminWarehouse).document(productControlID)
                .get().addOnSuccessListener {
                    if (it.exists()) {
                        val classFilter: DataClassProductsData? =
                            it.toObject(DataClassProductsData::class.java)

                        //load the image with Glide library
                        if (classFilter != null) {
                            Glide.with(requireActivity()).load(classFilter.imageProduct)
                                .into(imageViewCounterfeitProduct)
                            //loading other product details
                            textViewCounterfeitProductTitle.text = classFilter.title.toString()
                            textViewCounterfeitProductCode.text =
                                "code:\n" + classFilter.productID.toString()
                            textViewCounterfeitProductControllerID.text = "controlID:\n" +
                                    classFilter.timerControlID.toString()
                            textViewCounterfeitProductPlace.text =
                                "place:\n" + classFilter.university.toString()
                            textViewCounterfeitProductPhone.text =
                                "phone:\n" + classFilter.phone.toString()
                            //

                            //fun to check the  product details alterations in (phone number,place and image)
                            val imageWarehouse =
                                classFilter.imageProduct.toString() //original image
                            val phoneNumberWarehouse = classFilter.phone //original phone number
                            val placeWarehouse = classFilter.university //original place
                            //
                            funCheckChanges(imageWarehouse, phoneNumberWarehouse, placeWarehouse)
                            //

                        }
                        //
                    }

                }.addOnFailureListener {
                    funToastyFail("failed to load product data!")
                }
        }
        //code ends
    }

    @SuppressLint("SetTextI18n")
    private fun funCheckChanges(
        imageWarehouse: String,
        phoneNumberWarehouse: String?,
        placeWarehouse: String?
    ) {

        //code begins

        //fetch latest details from the public repo(product post/product control id) and make comparisons
        //on the images,phone and the place to that of the warehouse to detect if there is alterations

        val storePublicRepoPosts = FirebaseFirestore.getInstance()
        if (productControlID != null) {
            storePublicRepoPosts.collection(CollectionPost).document(productControlID).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val classFilter: DataClassProductsData? =
                            it.toObject(DataClassProductsData::class.java)

                        if (classFilter != null) {

                            val latestImage = classFilter.imageProduct
                            val latestPlace = classFilter.university
                            val latestPhone = classFilter.phone


                            //load the original image to the imageview using the glide library
                            Glide.with(requireActivity()).load(imageWarehouse)
                                .into(imageViewCounterfeitOriginalImage)
                            //
                            //load the newImage on its imageView(can be still the same image if was not changed by the suspect
                            Glide.with(requireActivity()).load(latestImage)
                                .into(imageViewCounterfeitNewImage)
                            //start making the comparisons of the place,phone and image

                            //image comparisons
                            if (imageWarehouse == latestImage) {
                                textViewCounterfeitIsImageAltered.text = "is image changed?:" +
                                        "\nNO"

                            } else {
                                textViewCounterfeitIsImageAltered.text = "is image changed?:" +
                                        "\nYES"

                            }

                            //phone comparison
                            if (phoneNumberWarehouse == latestPhone) {
                                textViewCounterfeitIsPhoneAltered.text = "is phone changed?:" +
                                        "\nNO"
                                textViewPhoneAlterationStatement.text =
                                    "phone number has not yet been changed\n" +
                                            "original phone ($phoneNumberWarehouse)\n" +
                                            "current phone($latestPhone)"

                            } else {
                                textViewCounterfeitIsPhoneAltered.text = "is phone changed?:" +
                                        "\nYES"
                                textViewPhoneAlterationStatement.text =
                                    "phone number has been changed\n" +
                                            "from original phone ($phoneNumberWarehouse)\n" +
                                            "to current phone($latestPhone)"
                            }

                            //place comparison
                            if (placeWarehouse == latestPlace) {
                                textViewCounterfeitIsPlaceAltered.text = "is place changed?:\nNO"
                                textViewPlaceAlterationStatement.text =
                                    "place has not yet been changed\n" +
                                            "original place ($placeWarehouse)\n" +
                                            "current place ($latestPlace)"

                            } else {
                                textViewCounterfeitIsPlaceAltered.text = "is place changed?:\nYES"
                                textViewPlaceAlterationStatement.text =
                                    "place has been changed" +
                                            "\nfrom original place ($placeWarehouse)" +
                                            "\nto current place ($latestPlace)"
                            }
                            //


                        }

                    }

                }.addOnFailureListener {
                    //toast failure
                    funToastyFail("failed to load data public collections")
                }
        }

        //code ends
    }

    private fun funInitGlobals(view: View) {
        //code
        textViewCounterfeitProductTitle = view.findViewById(R.id.tvCounterfeitProductTitle)
        textViewCounterfeitProductCode = view.findViewById(R.id.tvCounterfeitProductCode)
        textViewCounterfeitProductControllerID =
            view.findViewById(R.id.tvCounterfeitProductControllerID)
        textViewCounterfeitProductPlace = view.findViewById(R.id.tvCounterfeitProductPlace)
        textViewCounterfeitProductPhone = view.findViewById(R.id.tvCounterfeitProductPhone)
        textViewCounterfeitVictimName = view.findViewById(R.id.tvCounterfeitVictimName)
        textViewCounterfeitVictimPhone = view.findViewById(R.id.tvCounterfeitVictimPhone)
        textViewCounterfeitVictimEmail = view.findViewById(R.id.tvCounterfeitVictimEmail)
        textViewCounterfeitVictimPlace = view.findViewById(R.id.tvCounterfeitVictimPlace)
        textViewCounterfeitVictimRegDate = view.findViewById(R.id.tvCounterfeitVictimRegDate)
        textViewSuspectCounterfeitName = view.findViewById(R.id.tvSuspectCounterfeitName)
        textViewSuspectCounterfeitPhone = view.findViewById(R.id.tvSuspectCounterfeitPhone)
        textViewSuspectCounterfeitEmail = view.findViewById(R.id.tvSuspectCounterfeitEmail)
        textViewSuspectCounterfeitPlace = view.findViewById(R.id.tvSuspectCounterfeitPlace)
        textViewSuspectCounterfeitRegDate = view.findViewById(R.id.tvSuspectCounterfeitRegDate)
        textViewCounterfeitIsPhoneAltered = view.findViewById(R.id.tvCounterfeitIsPhoneAltered)
        textViewCounterfeitIsPlaceAltered = view.findViewById(R.id.tvCounterfeitIsPlaceAltered)
        textViewCounterfeitIsImageAltered = view.findViewById(R.id.tvCounterfeitIsImageAltered)
        textViewPhoneAlterationStatement = view.findViewById(R.id.tvPhoneAlterationStatement)
        textViewPlaceAlterationStatement = view.findViewById(R.id.tvPlaceAlterationStatement)
        imageViewCounterfeitProduct = view.findViewById(R.id.imgCounterfeitProduct)
        imageViewCounterfeitVictim = view.findViewById(R.id.imgCounterfeitVictim)
        imageViewSuspectCounterfeit = view.findViewById(R.id.imgSuspectCounterfeit)
        imageViewCounterfeitOriginalImage = view.findViewById(R.id.imgCounterfeitOriginalImage)
        imageViewCounterfeitNewImage = view.findViewById(R.id.imgCounterfeitNewImage)


        //code ens
    }


    //funCustomToastMore
    private fun funToastyCustomTwo(message: String, icon: Int, color: Int) {
        Toasty.custom(
            requireActivity(),
            message,
            icon,
            color,
            Toasty.LENGTH_SHORT,
            true,
            false
        ).show()
    }

    //fun customToast
    private fun funToastyCustom(message: String, icon: Int) {
        Toasty.custom(
            requireActivity(),
            message,
            icon,
            R.color.colorWhite,
            Toasty.LENGTH_SHORT,
            true,
            false
        ).show()
    }

    //function Toasty Fail
    private fun funToastyFail(message: String) {
        Toasty.custom(
            requireActivity(),
            message,
            R.drawable.ic_warning,
            R.color.androidx_core_secondary_text_default_material_light,
            Toasty.LENGTH_SHORT,
            true,
            false
        ).show()
    }

    //function Toasty Successful
    private fun funToastyShow(s: String) {
        Toasty.custom(
            requireActivity(),
            s,
            R.drawable.ic_nike_done,
            R.color.colorWhite,
            Toasty.LENGTH_SHORT,
            true,
            false
        ).show()
    }

}