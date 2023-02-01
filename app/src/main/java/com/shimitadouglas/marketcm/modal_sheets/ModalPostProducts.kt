package com.shimitadouglas.marketcm.modal_sheets

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.mains.ProductsHome.Companion.sharedPreferenceName
import com.shimitadouglas.marketcm.utilities.FileSizeDeterminant
import com.shimitadouglas.marketcm.utilities.ProductIDGenerator
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*
import kotlin.random.Random

class ModalPostProducts : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {
    companion object {
        const val CollectionPost = "Products Post"
        const val CollectionAdminWarehouse = "AdminPost WareHouse"

    }

    //globals
    private lateinit var appCompatButtonPickImage: AppCompatButton
    lateinit var appCompatButtonPost: AppCompatButton
    lateinit var appCompatButtonHint: AppCompatButton
    lateinit var circleImageViewShowProductImage: CircleImageView
    lateinit var spinnerPostProduct: Spinner
    lateinit var uriProduct: Uri
    lateinit var linearLayout: LinearLayout
    lateinit var editTextTitle: TextInputEditText
    lateinit var editTextDescription: TextInputEditText
    lateinit var editTextPrice: TextInputEditText


    //list category item
    private lateinit var spinnerData: String
    private val listOfCategory = arrayListOf<String>()
    //

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //code begins
        //init of the view
        val view: View = inflater.inflate(R.layout.modal_views_posting, container, false) as View
        appCompatButtonPickImage = view.findViewById(R.id.btnProvideImagePost)
        appCompatButtonPost = view.findViewById(R.id.btnPost)
        circleImageViewShowProductImage = view.findViewById(R.id.circleProvideImagePost)
        spinnerPostProduct = view.findViewById(R.id.spinnerPostProduct)
        linearLayout = view.findViewById(R.id.linearPostParentModal)
        editTextTitle = view.findViewById(R.id.edtTitleProductPost)
        editTextDescription = view.findViewById(R.id.edtDescriptionProductPost)
        editTextPrice = view.findViewById(R.id.edtPriceProductPost)
        appCompatButtonHint = view.findViewById(R.id.btnCheckHint)
        //inflating the array of the items in the adapter
        listOfCategory.add("PowerBanks")
        listOfCategory.add("Laptop RAM")
        listOfCategory.add("SmartPhones")
        listOfCategory.add("Tablets/Ipads")
        listOfCategory.add("SSDs/Hard Disk")
        listOfCategory.add("Tvs/FlatScreens")
        listOfCategory.add("Shoes/Drips/Dior")
        listOfCategory.add("Laptops/Desktops")
        listOfCategory.add("Flash Drive/SD Cards")
        listOfCategory.add("Earphones/HeadPhones")
        listOfCategory.add("Woofer/System/robot BT")
        listOfCategory.add("Kaduda/Mulika Mwizi/Kabambe")


        val adapter = ArrayAdapter<String>(
            requireActivity(), android.R.layout.simple_spinner_dropdown_item, listOfCategory
        )
        adapter.notifyDataSetChanged()
        spinnerPostProduct.adapter = adapter

        //setting listener on the spinner
        spinnerPostProduct.onItemSelectedListener = this@ModalPostProducts
        //


        //setting on Click Listener on buttons
        appCompatButtonPickImage.setOnClickListener {

            //animate button
            it.startAnimation(AnimationUtils.loadAnimation(requireActivity(), R.anim.bottom_up))
            //
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                //call function image show and upload
                funImageItemPost()
                //

            }, 1000)
        }

        appCompatButtonPost.setOnClickListener {
            //code begins
            //animate the button
            it.startAnimation(AnimationUtils.loadAnimation(requireActivity(), R.anim.bottom_up))
            //before making a post check if the user has verified his email address or not in order to continue
            val currentUser = FirebaseAuth.getInstance().currentUser
            //
            //check if the current user verified its email address. continue to post if the user verfied email
            val isEmailVerified = currentUser?.isEmailVerified

            if (isEmailVerified == true) {
                //user verified email continue to posting
                //call fun update/post
                funPostNow()
                //
            } else {
                //deny the user until email is verified
                funAlertPleaseVerifyEmail()
                //
            }

            //code ends
        }

        appCompatButtonHint.setOnClickListener {
            //alerting the user with hint on how to fill the title and description based on the item type
            if (spinnerData.contains("SmartPhones")) {
                //alert hint smartphone
                val random = Random.nextInt(6)
                val random2 = Random.nextInt(6)
                val nameList = listOf(
                    "Oppo Reno 8",
                    "Samsung Galaxy A13",
                    "Apple Iphone 7",
                    "Huawei Nova Y70",
                    "Tecno Camon 19",
                    "Xiami Redmi 10C"
                )

                val detailList = listOf(
                    "4Gb Ram, 32Gb Internal,good condition",
                    "6Gb Ram, 128Gb Internal,working fine",
                    "4gb Ram, 64gb Internal,minor screen crack",
                    "X Gb Ram, X Gb Internal,good condition",
                    "8gb Ram, 256gb Internal,working fine",
                    "X Gb Ram, XGb Internal,minor screen crack"
                )

                val priceList = arrayListOf(
                    "12,000",
                    "20,000",
                    "8,000",
                    "15,000",
                    "10,000",
                    "13,000"
                )
                val randomPrice = Random.nextInt(6)

                val randomIcons = Random.nextInt(1)
                val icon = listOf(R.drawable.phonee, R.drawable.phones)

                val title = "$spinnerData Suggestion"
                val name = nameList[random]
                val state = detailList[random2]
                val message =
                    "the title and description of your item could be:\n\ntitle:\n$name\n\ndescription:\n$state"
                //pass the items to the alert
                generalAlertHint(
                    title,
                    message,
                    icon[randomIcons],
                    name,
                    state,
                    priceList[randomPrice]
                )
                //
                //

            } else if (spinnerData.contains("Tablets")) {
                //alert hint tablet
                val random = Random.nextInt(6)
                val random2 = Random.nextInt(6)
                val nameList = listOf(
                    "Samsung Tablet",
                    "Apple Ipad",
                    "Lenovo Tablet",
                    "Tecno Tablet",
                    "Huawei Tablet",
                    "Vivo Tablet"
                )

                val detailList = listOf(
                    "4Gb Ram, 32Gb Internal,good condition",
                    "6Gb Ram, 128Gb Internal,working fine",
                    "4gb Ram, 64gb Internal,minor screen crack",
                    "X Gb Ram, X Gb Internal,good condition",
                    "8gb Ram, 256gb Internal,working fine",
                    "X Gb Ram, XGb Internal,minor screen crack"
                )
                val priceList = arrayListOf(
                    "12,000",
                    "20,000",
                    "8,000",
                    "15,000",
                    "10,000",
                    "13,000"
                )
                val randomPrice = Random.nextInt(6)

                val randomIcons = Random.nextInt(1)
                val icon = listOf(R.drawable.tablet, R.drawable.tabs)
                val title = "$spinnerData Suggestion"
                val name = nameList[random]
                val state = detailList[random2]
                val message =
                    "title and description of your item could be:\n\ntitle:\n$name\n\ndescription:\n$state"
                //pass the items to the alert
                generalAlertHint(
                    title,
                    message,
                    icon[randomIcons],
                    name,
                    state,
                    priceList[randomPrice]
                )
                //
                //

            } else if (spinnerData.contains("Tvs")) {
                //alert hint Tvs
                val random = Random.nextInt(6)
                val random2 = Random.nextInt(6)
                val nameList = listOf(
                    "Hisense Television ",
                    "Samsung Television",
                    "Sony Television",
                    "LG Television",
                    "Vitron Television",
                    "HTC Television"
                )

                val detailList = listOf(
                    "32 inches, in good condition",
                    "43 inches, working fine",
                    "19 inches, minor display issues",
                    "X  inches, watch comfortably,classic",
                    "X inches, working fine",
                    "X inches, minor power issues"
                )
                val priceList = arrayListOf(
                    "12,000",
                    "5,000",
                    "8,000",
                    "15,000",
                    "10,000",
                    "3,500"
                )
                val randomPrice = Random.nextInt(6)

                val randomIcons = Random.nextInt(1)
                val icon = listOf(R.drawable.tv, R.drawable.tv2)
                val title = "$spinnerData Suggestion"
                val name = nameList[random]
                val state = detailList[random2]
                val message =
                    "the title and description of your item could be:\n\ntitle:\n$name\n\ndescription:\n$state"
                //pass the items to the alert
                generalAlertHint(
                    title,
                    message,
                    icon[randomIcons],
                    name,
                    state,
                    priceList[randomPrice]
                )
                //
                //

            } else if (spinnerData.contains("Laptops")) {
                //alert hint laptops
                val random = Random.nextInt(6)
                val random2 = Random.nextInt(6)
                val nameList = listOf(
                    "Hp Probook 650 g2",
                    "Apple MacBook Pro 13",
                    "Asus X543U",
                    "Dell Latitude E7480",
                    "Lenovo Ideapad 3",
                    "Hp Elitebook 840P"
                )

                val detailList = listOf(
                    " Core i5,8Gb Ram,500Gb Hard Disk/SSD",
                    "Core i3,8Gb Ram,256Gb SSD,working fine",
                    "Intel Celeron,4Gb Ram,128Gb SSD/Hard Disk",
                    "Intel Celeron,XGb Ram,XGb SSD",
                    "Core i7,8Gb Ram,256Gb SSD,working fine",
                    "Core X,XGb Ram,XGb Hard Disk/SSD"
                )

                val priceList = arrayListOf(
                    "15,000",
                    "20,000",
                    "17,000",
                    "25,000",
                    "18,000",
                    "13,000"
                )
                val randomPrice = Random.nextInt(6)

                val randomIcons = Random.nextInt(2)
                val icon = listOf(R.drawable.lapsz, R.drawable.laptop)
                val title = "$spinnerData Suggestion"
                val name = nameList[random]
                val state = detailList[random2]
                val message =
                    "title and description of your item could be:\n\ntitle:\n$name\n\ndescription:\n$state"
                //pass the items to the alert
                generalAlertHint(
                    title,
                    message,
                    icon[randomIcons],
                    name,
                    state,
                    priceList[randomPrice]
                )
                //

                //code ends

            } else if (spinnerData.contains("Shoes")) {
                //reduce the counter size for shoes
                val textInputLayoutDe: TextInputLayout = view.findViewById(R.id.layoutDescription)
                val textInputLayoutT: TextInputLayout = view.findViewById(R.id.layoutTitlle)

                textInputLayoutDe.apply {
                    counterMaxLength = 50
                }
                textInputLayoutT.apply {
                    counterMaxLength = 20
                }
                //
                //alert hint shoes
                val random = Random.nextInt(6)
                val nameList = listOf(
                    "Airforce", "Timberlands", "Jordans", "Nikes", "Sneakers", "Rubber Shoes"
                )

                val detailList = listOf(
                    "stay classic,in good condition",
                    "shine wherever you go,brand New",
                    "let you shine,classic",
                    "rock as always,stay classic",
                    "change your looks,simple and attractive",
                    "stay undisputed,very brandy"
                )

                val priceList = arrayListOf(
                    "1,000",
                    "3,000",
                    "1,500",
                    "800",
                    "500",
                    "300"
                )
                val randomPrice = Random.nextInt(6)

                val randomIcons = Random.nextInt(2)
                val icon = listOf(R.drawable.shoe, R.drawable.shoes, R.drawable.shoesz)
                val title = "$spinnerData Suggestion"
                val name = nameList[random]
                val state = detailList[random]
                val message =
                    "title and description of your item could be:\n\ntitle:\n$name\n\ndescription:\n$state"
                //pass the items to the alert
                generalAlertHint(
                    title,
                    message,
                    icon[randomIcons],
                    name,
                    state,
                    priceList[randomPrice]
                )
                //
                //

            } else if (spinnerData.contains("Earphones")) {
                //alert hint earphones
                val random = Random.nextInt(6)
                val random2 = Random.nextInt(6)
                val nameList = listOf(
                    "Oraimo Earphones",
                    "Wired Headphones",
                    "Bluetooth Headphones",
                    "Sony Earphones",
                    "Samsung Earphones",
                    "Bluetooth Earphones"
                )

                val detailList = listOf(
                    "HD Sound,Still Brandy",
                    "Extra Bass,Brand New",
                    "HD Sound, good condition",
                    "Feel The Beat,Still Classic",
                    "Adjust volume to your demands,Classic",
                    "True Bass Extra,Still Brandy"
                )
                val priceList = arrayListOf(
                    "100",
                    "150",
                    "1000",
                    "500",
                    "400",
                    "300"
                )
                val randomPrice = Random.nextInt(6)

                val randomIcons = Random.nextInt(1)
                val icon = listOf(R.drawable.earphones, R.drawable.earp)
                val title = "$spinnerData Suggestion"
                val name = nameList[random]
                val state = detailList[random2]
                val message =
                    "title and description of your item could be:\n\ntitle:\n$name\n\ndescription:\n$state"
                //pass the items to the alert
                generalAlertHint(
                    title,
                    message,
                    icon[randomIcons],
                    name,
                    state,
                    priceList[randomPrice]
                )
                //
                //

            } else if (spinnerData.contains("Flash")) {
                //alert hint Flash
                val random = Random.nextInt(6)
                val random2 = Random.nextInt(6)
                val nameList = listOf(
                    "SanDisk Flash Drive",
                    "Samsung Flash Drive",
                    "Advance Memory Card",
                    "Samsung Memory Card",
                    "Hp FlashDisk",
                    "Advance FlashDisk Drive"
                )

                val detailList = listOf(
                    "32Gb,fast data transfer",
                    "8gb,Brand New,durable",
                    "64gb,fast data retrieval",
                    "128Gb,durable,fast",
                    "16Gb,fast,still brandy",
                    "4Gb,store documents safely"
                )

                val priceList = arrayListOf(
                    "350",
                    "2,000",
                    "1,500",
                    "1,000",
                    "500",
                    "800"
                )
                val randomPrice = Random.nextInt(6)

                val icon = R.drawable.flash
                val title = "$spinnerData Suggestion"
                val name = nameList[random]
                val state = detailList[random2]
                val message =
                    "title and description of your item could be:\n\ntitle:\n$name\n\ndescription:\n$state"
                //pass the items to the alert
                generalAlertHint(title, message, icon, name, state, priceList[randomPrice])
                //
                //
            } else if (spinnerData.contains("Woofer")) {
                //alert hint woofer
                val random = Random.nextInt(6)
                val random2 = Random.nextInt(6)
                val nameList = listOf(
                    "Amtec Woofer",
                    "Sony System",
                    "Von Woofer",
                    "Home Theatre System",
                    "Vitron SubWoofer",
                    "Robot BT Speaker"
                )

                val detailList = listOf(
                    "HD Sound With Equalizers,Still Brandy",
                    "Extra Bass,Brand New Stereo System",
                    "Quality Sound,in good condition",
                    "Quality Controlled Sound,Black,Still Classic",
                    "HD Vibrations,White,very Brandy",
                    "True Bass,White,Still Brandy"
                )

                val priceList = arrayListOf(
                    "2,000",
                    "3,000",
                    "5,000",
                    "1,800",
                    "4,500",
                    "2,500"
                )
                val randomPrice = Random.nextInt(6)

                val randomIcons = Random.nextInt(2)
                val icon = listOf(R.drawable.woofer, R.drawable.subwoofer, R.drawable.system)
                val title = "$spinnerData Suggestion"
                val name = nameList[random]
                val state = detailList[random2]
                val message =
                    "title and description of your item could be:\n\ntitle:\n$name\n\ndescription:\n$state"
                //pass the items to the alert
                generalAlertHint(
                    title,
                    message,
                    icon[randomIcons],
                    name,
                    state,
                    priceList[randomPrice]
                )
                //

                //
            } else if (spinnerData.contains("Kaduda")) {
                //alert hint Kaduda
                val random = Random.nextInt(6)
                val random2 = Random.nextInt(6)
                val nameList = listOf(
                    "Nokia", "Tecno", "Bontel", "Samsung", "Itel", "Uptel"
                )

                val detailList = listOf(
                    "supports Opera Mini,in good condition",
                    "black,working fine",
                    "White,no battery,in good condition",
                    "red in color,good condition",
                    "supports whatsapp,Opera,very classic",
                    "yellow,free memory card,working fine"
                )
                val priceList = arrayListOf(
                    "500",
                    "1,000",
                    "800",
                    "600",
                    "1,500",
                    "900"
                )
                val randomPrice = Random.nextInt(6)

                val randomIcons = Random.nextInt(1)
                val icon = listOf(R.drawable.kaduda, R.drawable.kabambe)
                val title = "$spinnerData Suggestion"
                val name = nameList[random]
                val state = detailList[random2]
                val message =
                    "title and description of your item could be:\n\ntitle:\n$name\n\ndescription:\n$state"
                //pass the items to the alert
                generalAlertHint(
                    title,
                    message,
                    icon[randomIcons],
                    name,
                    state,
                    priceList[randomPrice]
                )
                //
            } else if (spinnerData.contains("PowerBank")) {
                //alert hint Kaduda
                val random = Random.nextInt(6)
                val nameList = listOf(
                    "Oraimo PowerBank",
                    "Veger PowerBank",
                    "Samsung PowerBank",
                    "POHB PowerBank",
                    "Xiaomi Traveller PowerBank",
                    "Excellent PowerBank"
                )

                val detailList = listOf(
                    "20000Mah,Black,fast charger,in good condition",
                    "15000mah,1 charging port,white,stable in power",
                    "30000mah,4 charging ports,durable",
                    "12000mah,with LED lights,pink,fast Charger",
                    "25000mah,2 charging ports,supports fast charging",
                    "10000mah,with LED lights,charge phone twice"
                )
                val priceList = arrayListOf(
                    "2,000",
                    "2,500",
                    "1,000",
                    "800",
                    "500",
                    "1,800"
                )
                val randomPrice = Random.nextInt(6)

                val randomIcons = Random.nextInt(1)
                val icon = listOf(R.drawable.powez, R.drawable.power)
                val title = "$spinnerData Suggestion"
                val name = nameList[random]
                val state = detailList[random]
                val message =
                    "title and description of your item could be:\n\ntitle:\n$name\n\ndescription:\n$state"
                //pass the items to the alert
                generalAlertHint(
                    title,
                    message,
                    icon[randomIcons],
                    name,
                    state,
                    priceList[randomPrice]
                )

                //
            } else if (spinnerData.contains("SSD")) {
                //code begins
                val random = Random.nextInt(6)
                val random2 = Random.nextInt(6)
                val nameList = listOf(
                    "Sea Gate Hard Disk",
                    "Samsung SSD",
                    "Toshiba Hard Disk",
                    "Transcend Hard Disk",
                    "NVME SSD",
                    "Netac SSD"
                )

                val detailList = listOf(
                    "128Gb,fast data loading,Still Brandy",
                    "1Tb,lightening speed,Brand New",
                    "XGb, good condition",
                    "256Gb,fast data reading and writing capabilities",
                    "500Gb,very fast,Classic",
                    "320Gb,say no more hanging to the laptop"
                )
                val priceList = arrayListOf(
                    "5,000",
                    "2,000",
                    "2,500",
                    "3,000",
                    "4,000",
                    "3,500"
                )
                val randomPrice = Random.nextInt(6)

                val randomIcons = Random.nextInt(2)
                val icons = listOf(R.drawable.ssd1, R.drawable.ssd, R.drawable.hadd)

                val title = "$spinnerData Suggestion"
                val name = nameList[random]
                val state = detailList[random2]
                val message =
                    "title and description of your item could be:\n\ntitle:\n$name\n\ndescription:\n$state"
                //pass the items to the alert
                generalAlertHint(
                    title,
                    message,
                    icons[randomIcons],
                    name,
                    state,
                    priceList[randomPrice]
                )
                //
                //code
            } else if (spinnerData.contains("RAM")) {
                //alert hint Flash
                val random = Random.nextInt(6)
                val random2 = Random.nextInt(6)
                val nameList = listOf(
                    "Samsung RAM",
                    "Intel RAM",
                    "Crucial RAM",
                    "Fujitsu RAM",
                    "SK Hynix RAM",
                    "Corsair RAM"
                )

                val detailList = listOf(
                    "2Gb,PC3,DDR3",
                    "8Gb,PC3L,DDR3",
                    "16GB,PC3L,DDR4",
                    "4Gb,PC3L-12800S,DDR4",
                    "4Gb,PC3L,DDR4",
                    "8Gb,PC3-12800S,DDR3"
                )

                val priceList = arrayListOf(
                    "3,000",
                    "2,000",
                    "1,500",
                    "4,000",
                    "6,000",
                    "4,500"
                )
                val randomPrice = Random.nextInt(6)

                val icon = R.drawable.flash
                val title = "$spinnerData Suggestion"
                val name = nameList[random]
                val state = detailList[random2]
                val message =
                    "title and description of your item could be:\n\ntitle:\n$name\n\ndescription:\n$state"
                //pass the items to the alert
                generalAlertHint(title, message, icon, name, state, priceList[randomPrice])
                //
                //
            }
            //

        }
        //

        return view
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funAlertPleaseVerifyEmail() {
        //code begins
        //get the name of the user from the shared preference
        val sharedPreference =
            requireActivity().getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
        val firstName = sharedPreference.getString("firstname", "")
        val lastName = sharedPreference.getString("lastname", "")
        val fullName = "$firstName $lastName"
        //
        val emailAddress = FirebaseAuth.getInstance().currentUser?.email
        val alertVerifyEmail = MaterialAlertDialogBuilder(requireActivity())
        alertVerifyEmail.setIcon(R.drawable.ic_info)
        alertVerifyEmail.setTitle("Email Validation")
        alertVerifyEmail.background =
            resources.getDrawable(R.drawable.general_alert_dg, requireActivity().theme)
        alertVerifyEmail.setMessage(
            "$fullName your email address ($emailAddress)\n\nhas not yet been verified.\n" +
                    "\nverify your email address and post your products freely."
        )
        alertVerifyEmail.setPositiveButton("okay") { dialog, _ ->
            //dismiss
            dialog.dismiss()
            //
        }
        alertVerifyEmail.setCancelable(false)
        alertVerifyEmail.create()
        alertVerifyEmail.show()
        //code ends
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun funPostNow() {
        //code begins
        //obtaining the data from the all views before processing of their post
        val imageUriDataPost = uriProduct
        val titleDataPost = editTextTitle.text.toString()
        val descriptionDataPost = editTextDescription.text.toString()
        val priceDataPost = editTextPrice.text.toString()

        //check if price is > 4 and thn if misses comma inquire for it
        if (priceDataPost.length >= 4 && !priceDataPost.contains(",")) {
            Toasty.custom(
                requireActivity(),
                "separate price with comma(,)",
                R.drawable.ic_info,
                R.color.androidx_core_secondary_text_default_material_light,
                Toasty.LENGTH_SHORT,
                true,
                true
            ).show()
        } else {

            //check null presence in the data
            if (imageUriDataPost.equals("")) {
                Toast.makeText(
                    requireActivity(),
                    "image of the product is missing",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (uriProduct.equals("")) {
                Toast.makeText(
                    requireActivity(),
                    "image of the product is missing",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (TextUtils.isEmpty(titleDataPost)) {
                editTextTitle.error = "provide title of the item"
            } else if (TextUtils.isEmpty(descriptionDataPost)) {
                editTextDescription.error = "provide brief description of the item"
            } else if (TextUtils.isEmpty(priceDataPost)) {
                editTextPrice.error = "provide price of the item"
            } else if (priceDataPost.length > 4 && !priceDataPost.contains(",")) {
                editTextPrice.error = "separate price  with a comma (,)"
            }
            //everything fine
            else {
                //launch a coroutine to perform the network transactions
                funBeginPostingItem(
                    imageUriDataPost, titleDataPost, descriptionDataPost, priceDataPost, spinnerData
                )
                //
            }
            //
        }
        //code ends
    }

    private fun funBeginPostingItem(
        imageUriDataPost: Uri,
        titleDataPost: String,
        descriptionDataPost: String,
        priceDataPost: String,
        spinnerData: String
    ) {
        //creating a pD
        val progD = ProgressDialog(requireActivity())
        progD.apply {
            setTitle("Product Post")
            setCancelable(false)
            setMessage("posting...")
            create()
            show()
        }
        //

        //code begins
        //post image first then get the download uri
        //path to the storage product images=(ProductImages)/(Email)/(UID)/(combinationUIDTimerID)/(file)
        val parentChild = "ProductImages"
        val minorChildOneEmail = FirebaseAuth.getInstance().currentUser?.email
        val minorChildTwoUniQueUID = FirebaseAuth.getInstance().currentUser?.uid
        val minorChildThreeTimer = System.currentTimeMillis().toString()
        val combinationUIDTimerID = minorChildTwoUniQueUID + minorChildThreeTimer
        val firebaseStorage = FirebaseStorage.getInstance().reference
        if (minorChildOneEmail != null) {
            if (minorChildTwoUniQueUID != null) {

                firebaseStorage.child(parentChild).child(minorChildOneEmail)
                    .child(combinationUIDTimerID)
                    .putFile(imageUriDataPost).addOnCompleteListener {
                        if (it.isSuccessful) {
                            //successfully uploaded to the storage now obtain the download uro
                            it.result.storage.downloadUrl.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    //update UI pg and obtain download uri
                                    progD.setMessage("accepting...")
                                    //saving the string uri for storage it to fireStore
                                    val uriStringItemPosted = it.result.toString()
                                    //
                                    //call function to upload data to fStore
                                    funUploadPostStore(
                                        uriStringItemPosted,
                                        titleDataPost,
                                        descriptionDataPost,
                                        spinnerData,
                                        progD, priceDataPost
                                    )
                                    //
                                    //

                                } else if (!it.isSuccessful) {
                                    //failed to get download uri
                                    //alert fail
                                    val alertFailurePost =
                                        MaterialAlertDialogBuilder(requireActivity())
                                    alertFailurePost.apply {
                                        setTitle("Posting Failed")
                                        setMessage(it.exception?.message)
                                        setCancelable(false)
                                        setPositiveButton("retry") { dialog, _ ->

                                            //dismiss dialog to avoid RT errors
                                            dialog.dismiss()
                                            //
                                        }
                                        create()
                                        show()
                                    }
                                    //

                                    //dismiss the pg
                                    progD.dismiss()
                                    //


                                }
                            }

                        } //failed to upload to the fStorage
                        else if (!it.isSuccessful) {
                            //alert fail
                            val alertFailurePost = MaterialAlertDialogBuilder(requireActivity())
                            alertFailurePost.apply {
                                setTitle("Posting Failed")
                                setMessage(it.exception?.message)
                                setCancelable(false)
                                setPositiveButton("retry") { dialog, _ ->

                                    //dismiss
                                    dialog.dismiss()
                                    //
                                }
                                create()
                                show()
                            }
                            //

                            //dismiss the pg
                            progD.dismiss()
                            //

                        }
                    }
            }
        }
        //code ends

    }

    @SuppressLint("UseCompatLoadingForDrawables", "SimpleDateFormat")
    private fun funUploadPostStore(
        uriStringItemPosted: String,
        titleDataPost: String,
        descriptionDataPost: String,
        spinnerData: String,
        progD: ProgressDialog,
        priceDataPost: String
    ) {
        //code begins
        //generate a unique Product key on each item
        val productUniqueID = ProductIDGenerator.generateProductIDNow(20, true, true, true)
        val uniqueUID = FirebaseAuth.getInstance().uid
        //declaring the keys for the hashMap
        val keyImagePost = "imageProduct"
        val keyTitleItemPost = "title"
        val keyDescriptionItemPost = "description"
        val keyCategoryItemPost = "category"
        val keyProductID = "productID"
        val keyDate = "date"
        val keyPrice = "price"
        val keyUserID = "userID"

        //to be obtained from the shared pref
        val keyImageOwner = "imageOwner"
        val keyOwnerName = "Owner"
        val keyUniversity = "university"
        val keyPhoneNumber = "phone"
        val keyTimerId = "timerControlID"
        //

        //obtain user details saved in the fStore Cloud
        val sharedPreferences =
            requireActivity().getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)

        var fNameData = sharedPreferences.getString("firstname", "")
        var lNameData = sharedPreferences.getString("lastname", "")
        var imageData = sharedPreferences.getString("image", "")
        var phoneData = sharedPreferences.getString("phone", "")
        var uniData = sharedPreferences.getString("university", "")


        //creating instances for finding date of post
        val timeUsingCalendar = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
        val formattedTime = dateFormat.format(timeUsingCalendar)
        //
        //obtaining the timer for uniqueness of differentiating posts and also be delete the post
        //helps controlling the post i.e through update dof the post and delete of the post
        val timerID = System.currentTimeMillis().toString()
        //
        //path=(ProductPost)/(UID+timer)
        val uniqueUIDDocument = FirebaseAuth.getInstance().currentUser?.uid
        //document content path
        val combinationUIDTimer = uniqueUIDDocument + timerID
        //

        //create hashMap to store data fStore
        val mapItemPostDataFStore = hashMapOf(
            keyProductID to productUniqueID,
            keyCategoryItemPost to spinnerData,
            keyTitleItemPost to titleDataPost,
            keyDescriptionItemPost to descriptionDataPost,
            keyImagePost to uriStringItemPosted,
            keyDate to formattedTime.toString(),
            keyPrice to priceDataPost,
            keyUserID to uniqueUID,
            keyImageOwner to imageData,
            keyOwnerName to "$fNameData $lNameData",
            keyUniversity to uniData,
            keyPhoneNumber to phoneData,
            keyTimerId to combinationUIDTimer


        )

        //fStore Process

        val fStore = FirebaseFirestore.getInstance().collection(CollectionPost)
        //
        if (uniqueUIDDocument != null) {
            fStore.document(combinationUIDTimer).set(mapItemPostDataFStore).addOnCompleteListener {
                //successfully Uploaded data
                if (it.isSuccessful) {
                    //dismiss the pg and update user of congrats item posted
                    //now post to the user repo for  personal management
                    funPostToPersonalUserPosts(
                        progD,
                        mapItemPostDataFStore,
                        combinationUIDTimer,
                        productUniqueID
                    )
                    //
                } else if (!it.isSuccessful) {
                    //alert user failure
                    val alertFailurePost = MaterialAlertDialogBuilder(requireActivity())
                    alertFailurePost.apply {
                        setTitle("Posting Failed")
                        setMessage(it.exception?.message)
                        setCancelable(false)
                        setPositiveButton("retry") { dialog, _ ->

                            //dismiss
                            dialog.dismiss()
                            //
                        }
                        create()
                        show()
                    }
                    //

                    //dismiss the pg
                    progD.dismiss()
                    //
                }
            }
        }
        //
        //code end
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funPostToPersonalUserPosts(
        progD: ProgressDialog,
        mapItemPostDataFStore: HashMap<String, String?>,
        combinationUIDTimer: String,
        productUniqueID: String
    ) {
        //code begins
        //userPost path=>(uniqueUID/combinationUIDTimer/data)
        val userUIDCollection = FirebaseAuth.getInstance().uid
        //

        //
        val store = FirebaseFirestore.getInstance()
        //begin posting operations
        if (userUIDCollection != null) {
            store.collection(userUIDCollection).document(combinationUIDTimer)
                .set(mapItemPostDataFStore)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        //process of posting the in the personal repo successful
                        //change progress dialog to done
                        progD.setMessage("done...")
                        //
                        //create another collection which will be used during calamities of scamming.
                        //suspect might delete the details after malice activities thus having a backup store will make it easier
                        //to hunt such down
                        funPostAdminWareHouse(
                            mapItemPostDataFStore,
                            progD,
                            productUniqueID,
                            combinationUIDTimer
                        )
                        //

                    } else if (!it.isSuccessful) {
                        //error of posting to the current user posts collection
                        Toast.makeText(
                            requireActivity(),
                            "known error has occurred",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        //

                        //dismiss the progD
                        progD.dismiss()
                        //
                    }
                }
        }
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funPostAdminWareHouse(
        mapItemPostDataFStore: HashMap<String, String?>,
        progressDialogPost: ProgressDialog,
        productUniqueID: String,
        combinationUIDTimer: String
    ) {
        //code begins
        val storeAdmin = FirebaseFirestore.getInstance()
        storeAdmin.collection(CollectionAdminWarehouse).document(combinationUIDTimer)
            .set(mapItemPostDataFStore).addOnCompleteListener {

                if (it.isSuccessful) {
                    //posted successfully all data to the (publicRepo,private(userRepo,AdminWarehouseAll+Deletes)
                    progressDialogPost.apply {
                        //call fun congrats user of successful upload
                        val alertPostSuccessful = MaterialAlertDialogBuilder(requireActivity())
                        alertPostSuccessful.setTitle("posted successfully")
                        alertPostSuccessful.setIcon(R.drawable.ic_nike_done)
                        alertPostSuccessful.setCancelable(false)
                        alertPostSuccessful.background = resources.getDrawable(
                            R.drawable.material_six, requireActivity().theme
                        )
                        alertPostSuccessful.setMessage(
                            "product posted successfully to the online market interested customers will contact you about the product using your registered phone number via CALL,SMS or EMAIL\n" +
                                    "\n(PRODUCT ID=$productUniqueID)"
                        )
                        alertPostSuccessful.setPositiveButton("thanks") { dialog, _ ->
                            //dismiss the dialog and the modal
                            dialog.dismiss()
                            //
                        }
                        alertPostSuccessful.create()
                        alertPostSuccessful.show()
                        //dismiss pg
                        dismiss()
                        //
                    }


                } else if (!it.isSuccessful) {
                    //dismiss the progD
                    progressDialogPost.dismiss()
                    //
                    //did not post successfully
                    Toast.makeText(requireActivity(), "something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    //
                    return@addOnCompleteListener
                    //
                }
            }
        //code end
    }

    private fun funAlertFailureDialog(task: Task<UploadTask.TaskSnapshot>) {
        //code begins
        val alertFailurePost = MaterialAlertDialogBuilder(requireActivity())
        alertFailurePost.apply {
            setTitle("Posting Failed")
            setMessage(task.exception?.message)
            setCancelable(false)
            setPositiveButton("retry") { dialog, _ ->

                //dimiss
                dialog.dismiss()
                //
            }
            create()
            show()
        }
        //code ends
    }

    private fun funImageItemPost() {

        //code begins
        //check the permission of gallery READ and Write before starting
        //using the dexter library
        Dexter.withContext(requireActivity()).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                //launch intent activity to pick image of the product for posting
                val intentPickProductImage = Intent()
                intentPickProductImage.type = "image/*"
                intentPickProductImage.action = Intent.ACTION_PICK
                galleyActivity.launch(intentPickProductImage)
                //
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?, p1: PermissionToken?
            ) {
                //show dialog indicting the essence of the application to demand those permissions
                funShowAlertPermissionRationale()
                //
            }


        }).check()

        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funShowAlertPermissionRationale() {
        //code begins
        val alertPermissionRationale = MaterialAlertDialogBuilder(requireActivity())
        alertPermissionRationale.setTitle("Permissions")
        alertPermissionRationale.setIcon(R.drawable.ic_info)
        alertPermissionRationale.setMessage(
            "Market CM requires that the requested permissions are necessary for it to function properly." + " grant the permissions to use the application"
        )
        alertPermissionRationale.background =
            resources.getDrawable(R.drawable.material_six, requireActivity().theme)
        alertPermissionRationale.setCancelable(false)
        alertPermissionRationale.setPositiveButton("do") { dialog, _ ->
            //start the intent of launching the settings for app info
            val intentSettingsApp = Intent()
            intentSettingsApp.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            //
            dialog.dismiss()
            //
        }
        alertPermissionRationale.create()
        alertPermissionRationale.show()

        //code ends
    }

    private fun animLinearParentPartA() {
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            //
            val layoutAnimationController = LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    requireActivity(), R.anim.bottom_up_fast
                )
            )
            layoutAnimationController.order = LayoutAnimationController.ORDER_REVERSE
            linearLayout.layoutAnimation = layoutAnimationController
            linearLayout.startLayoutAnimation()
            //
        }, 1000)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private val galleyActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            //code begins
            if (it.resultCode == RESULT_OK) {
                if (it.data != null && it.data!!.data != null) {
                    //introduce file checker here to limit the size of item image posted to database cloud
                    val classFileChecker = FileSizeDeterminant(requireActivity())
                    val floatSizeOfItemBytes = classFileChecker.funGetSize(it.data!!.data)
                    val conversion = 1024.0f
                    val sizeItemKB = floatSizeOfItemBytes / conversion
                    val sizeLimit = 2.5f
                    //alert user if file size is greater than 2.5MB
                    if (sizeItemKB > 2500) {
                        alertUserFileSize(sizeItemKB, conversion, sizeLimit)
                    } else {
                        uriProduct = it.data!!.data!!

                        //alert User Congrats Image Pic Successfully
                        funAlertUserImageItemPickSucess(uriProduct)
                        //
                    }


                }

            } else if (it.resultCode == RESULT_CANCELED) {
                Toast.makeText(
                    requireActivity(),
                    "product image not provided\nyou won't be able to post without it",
                    Toast.LENGTH_LONG
                ).show()

                //disable button post since cannot post with no image
                appCompatButtonPost.visibility = View.INVISIBLE
                //
                //anim whole layout
                animLinearParentPartA()
                //
            }
            //ends
        }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funAlertUserImageItemPickSucess(uriProduct: Uri) {
        //code begins
        val alertCongrats = MaterialAlertDialogBuilder(requireActivity())
        alertCongrats.setTitle("Successful")
        alertCongrats.setIcon(R.drawable.cart)
        alertCongrats.setMessage(
            "Congratulations! image loaded successfully." + "Lets continue with product posting process, market your product online and earn your cash ."
        )
        alertCongrats.background =
            resources.getDrawable(R.drawable.material_six, requireActivity().theme)
        alertCongrats.setCancelable(false)
        alertCongrats.setPositiveButton("lets do it") { dialog, _ ->

            //call function to proceed image loading process to the UI front
            funProceedLoadImageItem(uriProduct)
            //
            dialog.dismiss()
            //
        }
        alertCongrats.create()
        alertCongrats.show()


        //code ends

    }

    private fun funProceedLoadImageItem(uriProduct: Uri) {
        //code begins
        //animate the view
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            //
            //enable the button post since product image is availed and also the whole view of image
            appCompatButtonPost.visibility = View.VISIBLE
            //
            //visibility true circleImage
            circleImageViewShowProductImage.visibility = View.VISIBLE
            circleImageViewShowProductImage.borderColor =
                resources.getColor(R.color.white, requireActivity().theme)
            //enable button hint
            appCompatButtonHint.visibility = View.VISIBLE
            //

            //
            //animate the image view here
            circleImageViewShowProductImage.startAnimation(
                AnimationUtils.loadAnimation(
                    requireActivity(), R.anim.rotate
                )
            )

            //animate whole
            animLinearParentPartB()
            //
            //
        }, 1000)
        //
        //

        //using glide library to load image onto the imageViewProduct
        Glide.with(requireActivity()).load(uriProduct).into(circleImageViewShowProductImage)
        //
        //code ends

    }

    private fun animLinearParentPartB() {
        //code begins
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            //
            val layoutAnimationController = LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    requireActivity(), R.anim.push_right_out
                )
            )
            layoutAnimationController.order = LayoutAnimationController.ORDER_REVERSE
            linearLayout.layoutAnimation = layoutAnimationController
            linearLayout.startLayoutAnimation()
            //
        }, 4500)
        //code ends
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        //code begins
        if (p0 != null) {
            //anim the suggestion btn for user interaction
            appCompatButtonHint.startAnimation(
                AnimationUtils.loadAnimation(
                    requireActivity(), R.anim.yobounce
                )
            )
            //
            //putting the value of the selected item in a bundle then we can access
            spinnerData = p0.selectedItem.toString()
            Toast.makeText(requireActivity(), spinnerData, Toast.LENGTH_SHORT).show()
            //
        }


        //code ends
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        //code begins
        return
        //
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun generalAlertHint(
        title: String, message: String, icon: Int, name: String, state: String, price: String
    ) {
        val alertMatHint = MaterialAlertDialogBuilder(requireActivity())
        alertMatHint.setTitle(title)
        alertMatHint.setMessage(message)
        alertMatHint.setIcon(icon)
        alertMatHint.background =
            resources.getDrawable(R.drawable.material_six, requireActivity().theme)
        alertMatHint.setCancelable(false)
        alertMatHint.setPositiveButton("Ok") { dialog, _ ->
            //dismiss dg to avoid RT Exceptions
            dialog.dismiss()
            //
        }
        alertMatHint.setNeutralButton("apply") { dialog, _ ->
            //call function to set text title and description for the user
            funDoFill(name, state, price)
            //
            //anim the suggestion btn for user interaction
            appCompatButtonHint.startAnimation(
                AnimationUtils.loadAnimation(
                    requireActivity(), R.anim.yobounce
                )
            )
            //
            //
            //dismiss
            dialog
            //
        }
        alertMatHint.create()
        alertMatHint.show()

    }

    private fun funDoFill(title: String, message: String, price: String) {
        //code begins
        editTextTitle.apply {
            setText(title)
        }
        editTextDescription.apply {
            setText(message)
        }
        editTextPrice.apply {
            setText(price)
        }
        //code ends

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun alertUserFileSize(fileSize: Float, conversion: Float, sizeLimit: Float) {
        //converting the size limit into thousands
        val limitKB = sizeLimit * 1000
        //
        val alertUserFileSize = MaterialAlertDialogBuilder(requireActivity())
        alertUserFileSize.setCancelable(false)
        alertUserFileSize.setIcon(R.drawable.ic_info)
        alertUserFileSize.background =
            resources.getDrawable(R.drawable.material_six, requireActivity().theme)
        alertUserFileSize.setTitle("Product Image Size")
        alertUserFileSize.setMessage(
            "size of the image of your product is larger than the recommended !\n" + "\nImage size of your product:\n${fileSize}KB  -> ${fileSize / conversion}MB\n" + "\nRecommended size of image:\n2500KB -> ${sizeLimit}MB\n\nImage exceeded limit by:${(fileSize - limitKB) / conversion}MB\n" + "\nConclusion:\nprovide an image less than ${sizeLimit}MB ."
        )
        alertUserFileSize.setPositiveButton("retry") { dialog, _ ->
            //dismiss the alert dg to avoid RT Errors
            dialog.dismiss()
            //
        }
        alertUserFileSize.create()
        alertUserFileSize.show()
    }


}





