package com.shimitadouglas.marketcm.fragmentProducts

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.adapter_products_posted.MyAdapterProducts
import com.shimitadouglas.marketcm.mains.ProductsHome.Companion.sharedPreferenceName
import com.shimitadouglas.marketcm.modal_data_posts.DataClassProductsData
import com.shimitadouglas.marketcm.notifications.BigPictureNotification
import es.dmoral.toasty.Toasty
import java.util.*

class HomeFragment : Fragment() {

    //string for holding selected uni for sorting products
    private var selected: String = ""
    //

    //init of the global
    private lateinit var viewHome: View
    lateinit var toolbarHome: Toolbar

    //late init var recyclerViewHome: RecyclerView
    private lateinit var collapsingToolbarLayoutHome: CollapsingToolbarLayout
    private lateinit var appBarLayoutHome: AppBarLayout
    private lateinit var floatingActionButtonHome: FloatingActionButton
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var imageSlider: ImageSlider

    //
    //recycler for Products hold
    lateinit var recyclerViewProducts: RecyclerView
    //

    // Declaration arrayList(ProductData) Adapter(MyAdapterProducts)
    lateinit var arrayListProducts: ArrayList<DataClassProductsData>
    private lateinit var adapterRecycler: MyAdapterProducts

    //
    //declaration of tempArrayList that will be used in searchView of type ProductData
    lateinit var tempArrayList: ArrayList<DataClassProductsData>

    //
    //arrayList Universities
    //init of globals
    private var universitiesForSort = arrayOf(
        "Maseno University",
        "Nairobi University",
        "Jomo Kenyatta University",
        "Kenyatta University",
        "Laikipia University",
        "Meru University",
        "Gretsa University",
        "DayStar University",
        "Garissa University",
        "Technical University Of Mombasa",
        "Taita Taveta University",
        "Pwani University",
        "Moi University",
        "Chuka University",
        "Kibabii University",
        "Saint Paul's University",
        "Maasai Mara University",
        "Alupe University",
        "Kisii University",
        "Adventist University Of Africa",
        "Africa International University",
        "Africa Nazarene University",
        "Amref International University",
        "Dedan Kimathi University",
        "Egerton University",
        "Great Lakes University",
        "International Leadership University",
        "Jaramogi Oginga Odinga University",
        "Kabarak University",
        "KAG University",
        "Karatina Universty",
        "KCA University",
        "Kenya Highlands University",
        "Kenya Methodist University",
        "Kirinyaga University",
        "Kirir Women's University",
        "Lukenya University",
        "Machakos University",
        "Management University Of Africa",
        "Masinde Muliro University",
        "Mount Kenya University",
        "Multimedia University ",
        "Murang'a University",
        "Pan Africa Christian University",
        "Pioneer International University",
        "RAF International University",
        "Riara University",
        "Rongo University",
        "Scott Christian University",
        "South Eastern Kenya University",
        "Strathmore University",
        "Technical university Of Kenya",
        "Catholic University Of Eastern Africa",
        "East African University",
        "Presbyterian University",
        "Umma University",
        "United States International university",
        "Baraton University",
        "Embu University",
        "Kabianga University",
        "Zetech University",
        "Uzima University",
        "University Of Eldoret",
        "Turkana University",
        "Tom Mboya University",
        "Tharaka University",
        "Tangaza University",
        "Koitaleel Samoei University",
        "Kaimosi Univesity",
        "SEKU University",
        "Bomet University",
        "Co-operative University Of Kenya",
        "Marist International University",
        "Management University of Africa"
    )

    //


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //code begins
        //init of the view
        viewHome = inflater.inflate(R.layout.home_fragment, container, false)
        //init of collapsing toolbar and other globals
        functionInit()
        //setting listener on the appbar using a function
        funListenerAppBarHome()
        //setting listener on the fab
        funFabListener()
        //
        //init the array list slide models in fun
        funArrayListSlideModels()
        //
        //init recyclerview ina fun on a separate thread
        val thread = Thread {
            Handler(Looper.getMainLooper()).post {
                funRecyclerOperations()
            }
        }
        thread.start()

        //
        //fun toolbarOperations
        funToolbarOperations()
        //
        //testing notification

        //funCheckNotification()

        //
        //code ends
        return viewHome
    }

    private fun funCheckNotification() {
        //code begins
        val icon = BitmapFactory.decodeResource(resources, R.drawable.ssd)
        val bigPicture = BigPictureNotification(
            requireActivity(),
            icon,
            "Market CM",
            "hey, Welcome",
            R.drawable.ic_cart,
            "BY:Shimmita",
            "Market CM"
        )
        bigPicture.funCreateBigPictureNotification()
        //code ends
    }

    private fun funToolbarOperations() {
        //code begins
        //set support has menu for the collapsing toolbar to work
        //pass the menu resource file using inflateMenu
        this.setHasOptionsMenu(true)
        this.toolbarHome.inflateMenu(R.menu.menu_collapse_toolbar)
        toolbarHome.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.searchProduct -> {
                    //call function search products
                    //alert the user, sho searching hint then on press ok initiate the actual search
                    //basing o the value returned from the shared pref
                    val sharedPref = requireActivity().getSharedPreferences(
                        sharedPreferenceName,
                        Context.MODE_PRIVATE
                    )
                    val resultFromSharedPref = sharedPref.getString("searchDialog", "")
                    if (resultFromSharedPref == "no") {
                        //do no show alertSearchHintDialog instead direct search
                        funSearchProducts()
                        //

                    } else {
                        //show the alert
                        funAlertSearch()
                    }

                    //
                }

                R.id.sortProducts -> {
                    //call function sorting products
                    funSortProducts()
                    //
                }
            }
            true
        }
        //
        //set a listener on toolbar such that when navIcon clicked it expands
        toolbarHome.setNavigationOnClickListener {
            //set appbar layout be expanded visible slider is
            appBarLayoutHome.setExpanded(true, true)
            //
        }
        //

        //code ends

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funAlertSearch() {
        //code begins
        val alertSearchHint = MaterialAlertDialogBuilder(requireActivity())
        alertSearchHint.background =
            resources.getDrawable(R.drawable.material_seven, requireActivity().theme)
        alertSearchHint.setTitle("Searching Hint")
        alertSearchHint.setIcon(R.drawable.ic_search)
        alertSearchHint.setMessage(
            "search using university name\n\n" + "search using product name\n\n" + "search using product type"
        )
        alertSearchHint.setCancelable(false)
        alertSearchHint.setNeutralButton("Ok search") { dialog, _ ->

            //call fun search
            funSearchProducts()
            //
            //dismiss dialog to avoid RT Exceptions
            dialog.dismiss()
            //
        }
        alertSearchHint.setPositiveButton("no show") { dg, _ ->

            AlertDialog.Builder(requireActivity())
                .setCancelable(false)
                .setIcon(R.drawable.ic_info)
                .setTitle("Note")
                .setMessage("Search hint dialog won't be shown again. if this is the case accept.")
                .setPositiveButton("accept") { dialog, _ ->
                    //save status in the shared preference
                    val sharedPref =
                        requireActivity().getSharedPreferences(
                            sharedPreferenceName,
                            Context.MODE_PRIVATE
                        )
                    sharedPref.edit().putString("searchDialog", "no").apply()
                    //dismiss
                    dialog.dismiss()
                    //

                    //dismiss the parent dialog too
                    dg.dismiss()
                    //
                }.setNegativeButton("dismiss") { dialog, _ ->

                    //dismiss to avoid RT Exceptions
                    dialog.dismiss()
                    //

                    //dismiss the parent dialog too
                    //dismiss
                    dg.dismiss()
                    //
                    //
                }
                .create().show()


        }
        alertSearchHint.create().show()
        //code ends
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun funRecyclerOperations() {
        //code begins
        val imageTempHolderOwner = R.drawable.dillan
        //
        //init arraylistProducts
        arrayListProducts = arrayListOf<DataClassProductsData>()
        //add using for loop for code easy than manual
        var i = 0
        while (i <= 100) {
            arrayListProducts.add(
                DataClassProductsData(
                    R.drawable.phones,
                    imageTempHolderOwner,
                    "Oppo Reno 8",
                    "Michael Angel",
                    "6Gb RAM,128GB internal,Working Perfect",
                    "Laptop RAM",
                    "67FRIDGES",
                    "Laikipia University",
                    "2023-01-15"
                )
            )
            i++
        }

        //init the tempArrayList
        tempArrayList = arrayListOf<DataClassProductsData>()
        //

        //add all the data of arrayListProducts into tempArrayList replicate it into the tempArrayList
        tempArrayList.addAll(arrayListProducts)
        //

        //init adapter(MyAdapter) use/pass tempArrayList for flexibility usage with searchView Operations
        adapterRecycler = MyAdapterProducts(tempArrayList, requireActivity())
        //
        //setting the adapter to the recycler
        recyclerViewProducts.adapter = adapterRecycler
        adapterRecycler.notifyDataSetChanged()
        //setting linearLayoutManager to the recycler
        recyclerViewProducts.layoutManager = LinearLayoutManager(requireActivity())
        //
        //

        //code ends

    }

    private fun funArrayListSlideModels() {
        //code begins
        val arrayListSlideModels = arrayListOf<SlideModel>()
        arrayListSlideModels.add(SlideModel(R.drawable.lapsz, "Laptops"))
        arrayListSlideModels.add(SlideModel(R.drawable.lap))
        arrayListSlideModels.add(SlideModel(R.drawable.tabs, "Tablets"))
        arrayListSlideModels.add(SlideModel(R.drawable.tablet))
        arrayListSlideModels.add(SlideModel(R.drawable.phones, "Smartphones"))
        arrayListSlideModels.add(SlideModel(R.drawable.phonee))
        arrayListSlideModels.add(SlideModel(R.drawable.kaduda, "Kabambe"))
        arrayListSlideModels.add(SlideModel(R.drawable.kabambe))
        arrayListSlideModels.add(SlideModel(R.drawable.power, "Powerbanks"))
        arrayListSlideModels.add(SlideModel(R.drawable.powez))
        arrayListSlideModels.add(SlideModel(R.drawable.hadd, "HDDs/SSDs"))
        arrayListSlideModels.add(SlideModel(R.drawable.ssd))
        arrayListSlideModels.add(SlideModel(R.drawable.shoesz, "Drips"))
        arrayListSlideModels.add(SlideModel(R.drawable.shoes))
        arrayListSlideModels.add(SlideModel(R.drawable.subwoofer, "Woofer"))
        arrayListSlideModels.add(SlideModel(R.drawable.woofer))
        imageSlider.setImageList(arrayListSlideModels, ScaleTypes.CENTER_CROP)

        //code ends

    }

    private fun funFabListener() {
        //code begins
        floatingActionButtonHome.setOnClickListener {
            //code begins
            //fab self anim
            floatingActionButtonHome.startAnimation(
                AnimationUtils.loadAnimation(
                    requireActivity(), R.anim.push_left_out
                )
            )
            //

            //appbar animate
            val homeAppBarContentRefreshAnim = LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    requireActivity(), R.anim.bottom_up
                )
            )
            homeAppBarContentRefreshAnim.delay = 2.0f
            homeAppBarContentRefreshAnim.order = LayoutAnimationController.ORDER_REVERSE
            appBarLayoutHome.layoutAnimation = homeAppBarContentRefreshAnim
            appBarLayoutHome.startLayoutAnimation()
            //

            //start fab functionality here after delay elapse
            floatingActionButtonHome.postDelayed({
                //code functionality herein
                //toasty that the refresh successfully done
                Toasty.normal(requireActivity(), "refreshed").show()
                //code ends

            }, 450)
            //end of fab functionality

            //code ends
        }
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funListenerAppBarHome() {
        //code begins
        var scrollRange = -1
        appBarLayoutHome.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = appBarLayout.totalScrollRange
            }
            if (scrollRange + verticalOffset == 0) {
                //collapsing toolbar fully collapsed and the toolbar should be given title
                collapsingToolbarLayoutHome.title = "hot products"
                //
                //set navigation icon hot toolbar
                toolbarHome.navigationIcon =
                    resources.getDrawable(R.drawable.ic_hot, requireActivity().theme)
                //

            } else {
                //the collapsing toolbar is expanded hence no ned to display the title text and hot icon
                collapsingToolbarLayoutHome.title = ""
                toolbarHome.background = null
                toolbarHome.navigationIcon = null
                //
            }
        }
        //code ends
    }

    @SuppressLint("PrivateResource")
    private fun functionInit() {
        //code  begins
        //call function to update the title accordingly
        val title = "Home"
        updateTitle(title)
        //init of variables and globals
        toolbarHome = viewHome.findViewById(R.id.toolbarHome)
        //recyclerViewHome = viewHome.findViewById(R.id.recyclerViewHomeProducts)
        collapsingToolbarLayoutHome = viewHome.findViewById(R.id.collapsingToolBarHome)
        appBarLayoutHome = viewHome.findViewById(R.id.appbarHome)
        floatingActionButtonHome = viewHome.findViewById(R.id.fabHome)
        coordinatorLayout = viewHome.findViewById(R.id.coordinatorHome)
        imageSlider = viewHome.findViewById(R.id.imageSlider)
        recyclerViewProducts = viewHome.findViewById(R.id.recyclerViewProducts)
        //

        //parent animate
        val parentLayoutHomeAnim = LayoutAnimationController(
            AnimationUtils.loadAnimation(
                requireActivity(), R.anim.rotate_avg
            )
        )
        parentLayoutHomeAnim.apply {
            parentLayoutHomeAnim.order = LayoutAnimationController.ORDER_REVERSE
            coordinatorLayout.layoutAnimation = parentLayoutHomeAnim
            coordinatorLayout.startLayoutAnimation()
        }

        //code ends
    }

    private fun funSearchProducts() {

        //code begins
        Toasty.custom(
            requireActivity(),
            "search",
            R.drawable.ic_search,
            R.color.androidx_core_secondary_text_default_material_light,
            Toasty.LENGTH_LONG,
            true,
            true
        ).show()
        //creating a menu item by id from toolbar
        val menu = this.toolbarHome.menu
        val menuItem: MenuItem = menu.findItem(R.id.searchProduct)
        //creating a searchView
        val searchView: SearchView = menuItem.actionView as SearchView
        searchView.queryHint = "type here to search products"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //called when user type and submits/ hit search
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                //animate the search behaviour such that anyTime
                //called anytime a text or character is typed inside the search
                //clear the tempArrayList before any operation
                tempArrayList.clear()
                //change the value of newText to lowercase and assign the result to a new string Variable
                val lowerCaseNewText = newText!!.lowercase(Locale.getDefault())
                //loop through the  original arrayList using for@ loop and check if values contained inside matches the lowercaseNewText
                //lowercase all the it contents to match all with the lowercaseNewText then if so add the results into the
                //tempArrayList which will be used for display
                if (lowerCaseNewText.isNotEmpty()) {
                    arrayListProducts.forEach {
                        if (it.titleProduct.lowercase(Locale.getDefault())
                                .contains(lowerCaseNewText)
                        ) {
                            //search contains data of title thus add it into the tempArrayList for display
                            //change the background color of it to show the user where search was found then add to tempArrayList

                            tempArrayList.add(it)
                            //

                        } else if (it.productOwner.lowercase(Locale.getDefault())
                                .contains(lowerCaseNewText)
                        ) {
                            //search contains owner of the product. add it into the tempArrayList for display
                            tempArrayList.add(it)
                            //

                        } else if (it.productID.lowercase(Locale.getDefault())
                                .contains(lowerCaseNewText)
                        ) {
                            //search contains product id. add it into the tempArrayList for display
                            tempArrayList.add(it)
                            //
                        } else if (it.vicinityProduct.lowercase(Locale.getDefault())
                                .contains(lowerCaseNewText)
                        ) {
                            //search contains vicinity/place of the product. add it into the tempArrayList for display
                            tempArrayList.add(it)
                            //
                        } else if (it.productDescription.lowercase(Locale.getDefault())
                                .contains(lowerCaseNewText)
                        ) {
                            //search contains description. add it into the temp ArrayList for display
                            tempArrayList.add(it)
                            //
                        } else if (it.categoryProduct.lowercase(Locale.getDefault())
                                .contains(lowerCaseNewText)
                        ) {
                            //search contains product type add it into the tempArrayList
                            tempArrayList.add(it)
                            //
                        }
                    }
                    //outside the for loop/ after looping notify the adapter that data has changed for it to get updated
                    recyclerViewProducts.adapter?.notifyDataSetChanged()
                    //
                } else {
                    //is empty lowercase search text or searchView is empty thus clear the tempArrayList and add the initial
                    //arraylist into the temp (replicate data again),notify recycler adapter for data to be displayed
                    //(finally reminder use the temp arrayList in the adapter)
                    tempArrayList.clear()
                    tempArrayList.addAll(arrayListProducts)
                    recyclerViewProducts.adapter?.notifyDataSetChanged()
                }
                //
                return false
            }
        })
        //

        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funSortProducts() {
        //code begins
        //alert dg showing the university selection
        //create a list that will display the options
        val listSortOptions = arrayOf(
            "Sort By University",
            "Sort By Product Category",
        )
        //
        val alertSortMethod = MaterialAlertDialogBuilder(requireActivity())
        alertSortMethod.setTitle("Select A Sorting Method")
        alertSortMethod.setCancelable(false)
        alertSortMethod.setIcon(R.drawable.ic_cart)
        alertSortMethod.background =
            resources.getDrawable(R.drawable.material_ten, requireActivity().theme)
        alertSortMethod.setSingleChoiceItems(listSortOptions, 2) { _, which ->
            //save the sorting option in a variable selected,be used for other evaluations
            selected = listSortOptions[which]
            //toast which sort method is selected
            //toast university sort method selected
            Toasty.custom(
                requireActivity(),
                selected,
                R.drawable.ic_nike_done,
                R.color.colorWhite,
                Toasty.LENGTH_SHORT,
                true,
                false
            ).show()
            //
            //
        }
        alertSortMethod.setPositiveButton("sort") { _, _ ->

            //code begins
            //check if empty is no selection before sorting
            if (selected.isNotEmpty()) {
                //check if university is the selection
                if (selected.contains("University")) {
                    //call function display University Names
                    functionSortByUniversity()
                    //
                } else if (selected.contains("Category")) {
                    Toasty.custom(
                        requireActivity(),
                        "Category Sort",
                        R.drawable.ic_nike_done,
                        R.color.colorWhite,
                        Toasty.LENGTH_SHORT,
                        true,
                        false
                    ).show()

                    //call alert show sort option by category
                    funSortByCategory()
                    //
                }
            } else if (selected.isEmpty()) {
                //show user must select a university
                Toasty.custom(
                    requireActivity(),
                    "hey, select a sorting method",
                    R.drawable.ic_smile,
                    R.color.colorWhite,
                    Toasty.LENGTH_SHORT,
                    true,
                    false
                ).show()
                //
            }
            //code ends
        }
        alertSortMethod.create()
        alertSortMethod.show()
        //

        //code ends
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funSortByCategory() {
        //code begins
        val listCategorySort = arrayOf(
            "Sort By PowerBanks",
            "Sort By Laptop RAM",
            "Sort By SmartPhones",
            "Sort By Tablets/Ipads",
            "Sort By Hard Disks/SSDs",
            "Sort By Laptops/Desktops",
            "Sort By TVs/FlatScreens",
            "Sort By Shoes/Drips/Dior",
            "Sort By Earphones/Headphones",
            "Sort By Woofer/System/robot BT",
            "Sort By Kaduda/Kabambe/Mulika Mwizi",
            "Sort By Flash Drives/SD Cards/Memory Cards",
        )
        var selectedCategoryType = ""

        val alertSortByCategory = MaterialAlertDialogBuilder(requireActivity())
        alertSortByCategory.setTitle("Select Category")
        alertSortByCategory.setIcon(R.drawable.ic_sort)
        alertSortByCategory.background =
            resources.getDrawable(R.drawable.material_eight, requireActivity().theme)
        alertSortByCategory.setSingleChoiceItems(listCategorySort, 0) { _, which ->
            selectedCategoryType = listCategorySort[which]
            //

            //toast to the user which sort category he/she opted for
            Toasty.custom(
                requireActivity(),
                selectedCategoryType,
                R.drawable.ic_nike_done,
                R.color.colorWhite,
                Toasty.LENGTH_SHORT,
                true,
                false
            ).show()
            //
        }
        alertSortByCategory.setNeutralButton("sortNow") { dialog, _ ->

            //code here the impact of the selected category Type
            if (selectedCategoryType.isEmpty()) {
                //code begins
                Toasty.custom(
                    requireActivity(),
                    "hey,select a category",
                    R.drawable.ic_smile,
                    R.color.colorWhite,
                    Toasty.LENGTH_SHORT,
                    true,
                    false
                ).show()
                //code ends

            } else if (selectedCategoryType.isNotEmpty()) {
                //basing on the category evaluate to e the correct response
                if (selectedCategoryType.lowercase(
                        Locale.getDefault()
                    ).contains("powerbanks")
                ) {
                    //call function to search powerbanks*
                    funSortByPowerBanks(selectedCategoryType)
                    //
                } else if (selectedCategoryType.lowercase(
                        Locale.getDefault()
                    ).contains("smartphones")
                ) {
                    //call function sort by smartphones
                    funSortBySmartPhones(selectedCategoryType)
                    //

                } else if (selectedCategoryType.lowercase(Locale.getDefault()).contains(
                        "tablets"
                    ) || selectedCategoryType.lowercase(Locale.getDefault()).contains("ipads")
                ) {
                    //call function sort by tablets/ipads
                    funSortByTabletsIpads(selectedCategoryType)
                    //
                } else if (selectedCategoryType.lowercase(Locale.getDefault())
                        .contains("laptops") || selectedCategoryType.lowercase(Locale.getDefault())
                        .contains("desktops")
                ) {
                    //call fun sort by laptops
                    funSortByLaptopDesktops(selectedCategoryType)
                    //

                } else if (selectedCategoryType.lowercase(Locale.getDefault())
                        .contains("tvs") || selectedCategoryType.lowercase(
                        Locale.getDefault()
                    ).contains("flatscreens")
                ) {
                    //fun sort by tvs.flatscreens
                    funSortByTvFlatScreen(selectedCategoryType)
                    //

                } else if (selectedCategoryType.lowercase(Locale.getDefault())
                        .contains("shoes") || selectedCategoryType.lowercase(
                        Locale.getDefault()
                    ).contains("drips") || selectedCategoryType.lowercase(
                        Locale.getDefault()
                    ).contains("dior")
                ) {
                    //fun sort by shoes dior
                    funSortByShoesDiorDrip(selectedCategoryType)
                    //

                } else if (selectedCategoryType.lowercase(Locale.getDefault())
                        .contains("earphones") || selectedCategoryType.lowercase(
                        Locale.getDefault()
                    ).contains("headphones")
                ) {
                    //fun sort by earphones headphones
                    funSortByEarphoneHeadPhones(selectedCategoryType)
                    //

                } else if (selectedCategoryType.lowercase(Locale.getDefault())
                        .contains("woofer") || selectedCategoryType.lowercase(
                        Locale.getDefault()
                    ).contains("system") || selected.lowercase(Locale.getDefault()).contains("bt")
                ) {

                    //fun sort by woofer system robot BT
                    funSortByWooferSystemRobotBT(selectedCategoryType)
                    //

                } else if (selectedCategoryType.lowercase(Locale.getDefault())
                        .contains("kaduda") || selectedCategoryType.lowercase(
                        Locale.getDefault()
                    ).contains("kabambe") || selectedCategoryType.lowercase(
                        Locale.getDefault()
                    ).contains("mwizi")
                ) {

                    //fun sort by kabambe kaduda mulika mwizi
                    funSortByKabambeKaduda(selectedCategoryType)
                    //

                } else if (selectedCategoryType.lowercase(Locale.getDefault())
                        .contains("flash") || selectedCategoryType.lowercase(
                        Locale.getDefault()
                    ).contains("cards") || selectedCategoryType.lowercase(
                        Locale.getDefault()
                    ).contains("memory")
                ) {
                    //sort By flash drives SD cards
                    funSortByFlashSD(selectedCategoryType)
                    //

                } else if (selectedCategoryType.lowercase(Locale.getDefault())
                        .contains("hard") || selectedCategoryType.contains("ssd")
                ) {
                    Toast.makeText(requireActivity(), "hard disks/ssds", Toast.LENGTH_SHORT).show()

                    //fun sort by HDD or SSDs
                    funSortByHDDSSD(selectedCategoryType)
                    //
                } else if (selectedCategoryType.lowercase(Locale.getDefault()).contains("ram")) {
                    Toast.makeText(requireActivity(), "Laptop RAMS", Toast.LENGTH_SHORT).show()
                    //call function sort items by RAMS
                    funSortBYRAM(selectedCategoryType)
                    //

                }

                //
            }
            //
        }
        alertSortByCategory.create()
        alertSortByCategory.show()
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    private fun funSortBYRAM(selectedCategoryType: String) {
        //code begins
        val tempArrayRAMS = arrayListOf<DataClassProductsData>()
        //clear the the list
        tempArrayRAMS.clear()
        //iterate through the list original temp and save the found RAMs into the tempRAMS
        arrayListProducts.forEach {
            if (it.categoryProduct.lowercase(Locale.getDefault()).contains("ram")) {
                tempArrayRAMS.add(it)
            }
        }

        if (tempArrayRAMS.isEmpty()) {
            val alertNoData = MaterialAlertDialogBuilder(requireActivity())
            alertNoData.setIcon(R.drawable.ic_sort)
            alertNoData.setTitle("$selectedCategoryType Results")
            alertNoData.setCancelable(false)
            alertNoData.background =
                resources.getDrawable(R.drawable.material_seven, requireActivity().theme)
            alertNoData.setMessage(
                "laptop RAM(s) have not yet been posted.you might become the first one to post if you do posting on Laptop RAMS" + "\nan opportunity huh? grab that opportunity!\n" + "try using the search for better results!"
            )
            alertNoData.setNeutralButton("ok") { dialog, _ ->

                //dismiss the dialog
                dialog.dismiss()
                //
            }
            alertNoData.create()
            alertNoData.show()
        } else if (tempArrayRAMS.isNotEmpty()) {
            //code begins
            val adapterRAMS = MyAdapterProducts(tempArrayRAMS, requireActivity())
            recyclerViewProducts.adapter = null
            recyclerViewProducts.adapter = adapterRAMS
            recyclerViewProducts.layoutManager = LinearLayoutManager(requireActivity())
            adapterRAMS.notifyDataSetChanged()
            //code ends

        }
        //
        //code ends
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    private fun funSortByHDDSSD(selectedCategoryType: String) {

        //code begins
        val tempArrayListHDDSSD = arrayListOf<DataClassProductsData>()
        tempArrayListHDDSSD.clear()
        tempArrayList.forEach {
            if (it.categoryProduct.lowercase(Locale.getDefault())
                    .contains("SSD") || it.categoryProduct.lowercase(
                    Locale.getDefault()
                ).contains("Hard")
            ) {
                tempArrayListHDDSSD.add(it)
            }
        }
        if (tempArrayListHDDSSD.isEmpty()) {
            val alertNoData = MaterialAlertDialogBuilder(requireActivity())
            alertNoData.setIcon(R.drawable.ic_sort)
            alertNoData.setTitle("$selectedCategoryType Results")
            alertNoData.setCancelable(false)
            alertNoData.background =
                resources.getDrawable(R.drawable.material_seven, requireActivity().theme)
            alertNoData.setMessage(
                "hard disks or SSDs have not yet been posted.you might become the first one to post if you do posting on hard disks or SSDs" + "\nan opportunity huh? grab that opportunity!\n" + "try using the search for better results!"
            )
            alertNoData.setNeutralButton("ok") { dialog, _ ->

                //dismiss the dialog
                dialog.dismiss()
                //
            }
            alertNoData.create()
            alertNoData.show()
        } else if (tempArrayListHDDSSD.isNotEmpty()) {
            recyclerViewProducts.adapter = null
            val adapterHDDSSD = MyAdapterProducts(tempArrayListHDDSSD, requireActivity())
            recyclerViewProducts.adapter = adapterHDDSSD
            recyclerViewProducts.layoutManager = LinearLayoutManager(requireActivity())
            adapterHDDSSD.notifyDataSetChanged()
        }
        //code ends
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    private fun funSortByFlashSD(selectedCategoryType: String) {

        //code begins
        val tempArrayListFlashSD = arrayListOf<DataClassProductsData>()
        tempArrayListFlashSD.clear()
        tempArrayList.forEach {
            if (it.categoryProduct.lowercase(Locale.getDefault())
                    .contains("flash") || it.categoryProduct.lowercase(
                    Locale.getDefault()
                ).contains("card")
            ) {
                tempArrayListFlashSD.add(it)
            }
        }

        if (tempArrayListFlashSD.isEmpty()) {
            val alertNoData = MaterialAlertDialogBuilder(requireActivity())
            alertNoData.setIcon(R.drawable.ic_sort)
            alertNoData.setTitle("$selectedCategoryType Results")
            alertNoData.setCancelable(false)
            alertNoData.background =
                resources.getDrawable(R.drawable.material_seven, requireActivity().theme)
            alertNoData.setMessage(
                "flash drives,Pen Drives,SD Cards or Memory Cards have not yet been posted.you might become the first one to post if you do posting on Flash Drives,SD Cards" + "\nan opportunity huh? grab that opportunity!\n" + "try using the search for better results!"
            )
            alertNoData.setNeutralButton("ok") { dialog, _ ->

                //dismiss the dialog
                dialog.dismiss()
                //
            }
            alertNoData.create()
            alertNoData.show()
        } else if (tempArrayListFlashSD.isNotEmpty()) {
            recyclerViewProducts.adapter = null
            val adapterFlashDrives = MyAdapterProducts(tempArrayListFlashSD, requireActivity())
            recyclerViewProducts.adapter = adapterFlashDrives
            recyclerViewProducts.layoutManager = LinearLayoutManager(requireActivity())
            adapterFlashDrives.notifyDataSetChanged()
        }
        //code ends
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    private fun funSortByKabambeKaduda(selectedCategoryType: String) {

        //code begins
        val tempArrayListKabambeKaduda = arrayListOf<DataClassProductsData>()
        tempArrayListKabambeKaduda.clear()
        tempArrayList.forEach {

            if (it.categoryProduct.lowercase(Locale.getDefault())
                    .contains("kabambe") || it.categoryProduct.lowercase(
                    Locale.getDefault()
                ).contains("kaduda") || it.categoryProduct.contains("mulika")
            ) {
                tempArrayListKabambeKaduda.add(it)
            }
        }
        if (tempArrayListKabambeKaduda.isEmpty()) {
            val alertNoData = MaterialAlertDialogBuilder(requireActivity())
            alertNoData.setIcon(R.drawable.ic_sort)
            alertNoData.setTitle("$selectedCategoryType Results")
            alertNoData.background =
                resources.getDrawable(R.drawable.material_seven, requireActivity().theme)
            alertNoData.setCancelable(false)
            alertNoData.setMessage(
                "kabambe or kaduda have not yet been posted.you might become the first one to post if you do posting on kabambe or kaduda" + "\nan opportunity huh? grab that opportunity!\n" + "try using the search for better results!"
            )
            alertNoData.setNeutralButton("ok") { dialog, _ ->

                //dismiss the dialog
                dialog.dismiss()
                //
            }
            alertNoData.create()
            alertNoData.show()
        } else if (tempArrayListKabambeKaduda.isNotEmpty()) {
            recyclerViewProducts.adapter = null
            val adapterKabambeKaduda =
                MyAdapterProducts(tempArrayListKabambeKaduda, requireActivity())
            recyclerViewProducts.adapter = adapterKabambeKaduda
            recyclerViewProducts.layoutManager = LinearLayoutManager(requireActivity())
            adapterKabambeKaduda.notifyDataSetChanged()

        }
        //code
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funSortByWooferSystemRobotBT(selectedCategoryType: String) {

        //code begins
        val tempArrayListWooferSystem = arrayListOf<DataClassProductsData>()
        tempArrayListWooferSystem.clear()
        tempArrayList.forEach {
            if (it.categoryProduct.lowercase(Locale.getDefault())
                    .contains("woofer") || it.categoryProduct.lowercase(
                    Locale.getDefault()
                ).contains("system")
            ) {
                tempArrayListWooferSystem.add(it)
            }

        }
        if (tempArrayListWooferSystem.isEmpty()) {
            val alertNoData = MaterialAlertDialogBuilder(requireActivity())
            alertNoData.setIcon(R.drawable.ic_sort)
            alertNoData.setTitle("$selectedCategoryType Results")
            alertNoData.setCancelable(false)
            alertNoData.background =
                resources.getDrawable(R.drawable.material_seven, requireActivity().theme)
            alertNoData.setMessage(
                "woofer,subwoofer or robot speakers have not yet been posted.you might become the first one to post if you do posting on woofer,subwoofer or robot BT" + "\nan opportunity huh? grab that opportunity!\n" + "try using the search for better results!"
            )
            alertNoData.setNeutralButton("ok") { dialog, _ ->

                //dismiss the dialog
                dialog.dismiss()
                //
            }
            alertNoData.create()
            alertNoData.show()
        }

        //code ends
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    private fun funSortByEarphoneHeadPhones(selectedCategoryType: String) {

        //code begins
        val tempArrayListEarphonesHeadPhones = arrayListOf<DataClassProductsData>()
        tempArrayListEarphonesHeadPhones.clear()
        tempArrayList.forEach {

            if (it.categoryProduct.lowercase(Locale.getDefault())
                    .contains("earphones") || it.categoryProduct.lowercase(
                    Locale.getDefault()
                ).contains("headphones")
            ) {
                tempArrayListEarphonesHeadPhones.add(it)
            }
        }
        if (tempArrayListEarphonesHeadPhones.isEmpty()) {
            val alertNoData = MaterialAlertDialogBuilder(requireActivity())
            alertNoData.setIcon(R.drawable.ic_search)
            alertNoData.setTitle("$selectedCategoryType Results")
            alertNoData.setCancelable(false)
            alertNoData.background =
                resources.getDrawable(R.drawable.material_seven, requireActivity().theme)
            alertNoData.setMessage(
                "earphones or headphones have not yet been posted.you might become the first one to post if you do posting on earphones or headphones" + "\nan opportunity huh? grab that opportunity!\n" + "try using the search for better results!"
            )
            alertNoData.setNeutralButton("ok") { dialog, _ ->

                //dismiss the dialog
                dialog.dismiss()
                //
            }
            alertNoData.create()
            alertNoData.show()
        } else if (tempArrayListEarphonesHeadPhones.isNotEmpty()) {
            recyclerViewProducts.adapter = null
            val adapterEarphonesHeadPhones =
                MyAdapterProducts(tempArrayListEarphonesHeadPhones, requireActivity())
            recyclerViewProducts.adapter = adapterEarphonesHeadPhones
            recyclerViewProducts.layoutManager = LinearLayoutManager(requireActivity())
            adapterEarphonesHeadPhones.notifyDataSetChanged()
        }
        //code ends
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    private fun funSortByShoesDiorDrip(selectedCategoryType: String) {
        //code begins
        val tempArrayListShoesDripsDior = arrayListOf<DataClassProductsData>()
        tempArrayListShoesDripsDior.clear()
        tempArrayList.forEach {
            if (it.categoryProduct.lowercase(Locale.getDefault())
                    .contains("shoes") || it.categoryProduct.lowercase(
                    Locale.getDefault()
                ).contains("dior") || it.categoryProduct.contains("drip")
            ) {
                tempArrayListShoesDripsDior.add(it)
            }
        }
        if (tempArrayListShoesDripsDior.isEmpty()) {
            val alertNoData = MaterialAlertDialogBuilder(requireActivity())
            alertNoData.setIcon(R.drawable.ic_search)
            alertNoData.setTitle("$selectedCategoryType Results")
            alertNoData.setCancelable(false)
            alertNoData.background =
                resources.getDrawable(R.drawable.material_seven, requireActivity().theme)
            alertNoData.setMessage(
                "shoes,drips or dior have not yet been posted.you might become the first one to post if you do posting on shoes,drips or dior" + "\nan opportunity huh? grab that opportunity!\n" + "try using the search for better results!"
            )
            alertNoData.setNeutralButton("ok") { dialog, _ ->

                //dismiss the dialog
                dialog.dismiss()
                //
            }
            alertNoData.create()
            alertNoData.show()
        } else if (tempArrayListShoesDripsDior.isNotEmpty()) {
            recyclerViewProducts.adapter = null
            val adapterShoesDripsDior =
                MyAdapterProducts(tempArrayListShoesDripsDior, requireActivity())
            recyclerViewProducts.adapter = adapterShoesDripsDior
            recyclerViewProducts.layoutManager = LinearLayoutManager(requireActivity())
            adapterShoesDripsDior.notifyDataSetChanged()
        }
        //code ends
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    private fun funSortByTvFlatScreen(selectedCategoryType: String) {
        //code begins
        val tempArrayListTvsFlats = arrayListOf<DataClassProductsData>()
        tempArrayListTvsFlats.clear()
        tempArrayList.forEach {
            if (it.categoryProduct.lowercase(Locale.getDefault())
                    .contains("tvs") || it.categoryProduct.lowercase(
                    Locale.getDefault()
                ).contains("flatscreen")
            ) {
                tempArrayListTvsFlats.add(it)
            }
        }
        if (tempArrayListTvsFlats.isEmpty()) {
            val alertNoData = MaterialAlertDialogBuilder(requireActivity())
            alertNoData.setIcon(R.drawable.ic_search)
            alertNoData.setTitle("$selectedCategoryType Results")
            alertNoData.background =
                resources.getDrawable(R.drawable.material_seven, requireActivity().theme)
            alertNoData.setCancelable(false)
            alertNoData.setMessage(
                "tvs or flatscreens have not yet been posted.you might become the first one to post if you do posting on tvs or flatscreens" + "\nan opportunity huh? grab that opportunity!\n" + "try using the search for better results!"
            )
            alertNoData.setNeutralButton("ok") { dialog, _ ->

                //dismiss the dialog
                dialog.dismiss()
                //
            }
            alertNoData.create()
            alertNoData.show()
        } else if (tempArrayListTvsFlats.isNotEmpty()) {
            recyclerViewProducts.adapter = null
            val adapterTvsFlats = MyAdapterProducts(tempArrayListTvsFlats, requireActivity())
            recyclerViewProducts.adapter = adapterTvsFlats
            recyclerViewProducts.layoutManager = LinearLayoutManager(requireActivity())
            adapterTvsFlats.notifyDataSetChanged()
        }
        //code ens
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    private fun funSortByLaptopDesktops(selectedCategoryType: String) {
        //code begins
        val tempArrayListLaptopDeskTops = arrayListOf<DataClassProductsData>()
        tempArrayListLaptopDeskTops.clear()
        tempArrayList.forEach {
            if (it.categoryProduct.lowercase(Locale.getDefault())
                    .contains("laptop") || it.categoryProduct.lowercase(
                    Locale.getDefault()
                ).contains("desktop")
            ) {
                tempArrayListLaptopDeskTops.add(it)
            }
        }
        if (tempArrayListLaptopDeskTops.isEmpty()) {
            val alertNoData = MaterialAlertDialogBuilder(requireActivity())
            alertNoData.setIcon(R.drawable.ic_search)
            alertNoData.setTitle("$selectedCategoryType Results")
            alertNoData.setCancelable(false)
            alertNoData.background =
                resources.getDrawable(R.drawable.material_seven, requireActivity().theme)
            alertNoData.setMessage(
                "laptops and Desktops have not yet been posted.you might become the first one to post if you do posting on laptops and desktops" + "\nan opportunity huh? grab that opportunity!\n" + "try using the search for better results!"
            )
            alertNoData.setNeutralButton("ok") { dialog, _ ->

                //dismiss the dialog
                dialog.dismiss()
                //
            }
            alertNoData.create()
            alertNoData.show()
        } else if (tempArrayListLaptopDeskTops.isNotEmpty()) {
            recyclerViewProducts.adapter = null
            val adapterLaptopDesktop =
                MyAdapterProducts(tempArrayListLaptopDeskTops, requireActivity())
            recyclerViewProducts.adapter = adapterLaptopDesktop
            recyclerViewProducts.layoutManager = LinearLayoutManager(requireActivity())
            adapterLaptopDesktop.notifyDataSetChanged()
        }
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    private fun funSortByTabletsIpads(selectedCategoryType: String) {
        //code begins
        val tempArrayListTabletsIpads = arrayListOf<DataClassProductsData>()
        tempArrayListTabletsIpads.clear()

        arrayListProducts.forEach {
            if (it.categoryProduct.lowercase(Locale.getDefault())
                    .contains("tablets") || it.categoryProduct.lowercase(
                    Locale.getDefault()
                ).contains("ipad")
            ) {
                tempArrayListTabletsIpads.add(it)
            }
        }
        if (tempArrayListTabletsIpads.isEmpty()) {
            val alertNoData = MaterialAlertDialogBuilder(requireActivity())
            alertNoData.setIcon(R.drawable.ic_search)
            alertNoData.setTitle("$selectedCategoryType Results")
            alertNoData.background =
                resources.getDrawable(R.drawable.material_seven, requireActivity().theme)
            alertNoData.setCancelable(false)
            alertNoData.setMessage(
                "tablets and ipads have not yet been posted.you might become the first one to post if you do posting on tablets and ipads" + "\nan opportunity huh? grab that opportunity!\n" + "try using the search for better results!"
            )
            alertNoData.setNeutralButton("ok") { dialog, _ ->

                //dismiss the dialog
                dialog.dismiss()
                //
            }
            alertNoData.create()
            alertNoData.show()
        } else if (tempArrayListTabletsIpads.isNotEmpty()) {
            recyclerViewProducts.adapter = null
            val adapterTabIpad = MyAdapterProducts(tempArrayListTabletsIpads, requireActivity())
            recyclerViewProducts.adapter = adapterTabIpad
            recyclerViewProducts.layoutManager = LinearLayoutManager(requireActivity())
            adapterTabIpad.notifyDataSetChanged()
        }
        //code ends
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    private fun funSortBySmartPhones(selectedCategoryType: String) {

        //code begins
        //create a temp arrayList for smartphones
        val tempArrayListSmartPhones = arrayListOf<DataClassProductsData>()
        //clear the list
        tempArrayListSmartPhones.clear()
        //use for each loop to loop through smartphones on orig tempArray whilst adding the results in our
        //custom temp of smartPhones
        tempArrayList.forEach {

            if (it.categoryProduct.lowercase(Locale.getDefault()).contains("smartphones")) {
                //smartphones data present
                tempArrayListSmartPhones.add(it)
                //
            }
        }

        //check if empty tempArrayList Products and react accordingly
        if (tempArrayListSmartPhones.isEmpty()) {
            //alert empty results about smartphones
            val alertNoData = MaterialAlertDialogBuilder(requireActivity())
            alertNoData.setIcon(R.drawable.ic_search)
            alertNoData.setTitle("$selectedCategoryType Results")
            alertNoData.background =
                resources.getDrawable(R.drawable.material_seven, requireActivity().theme)
            alertNoData.setCancelable(false)
            alertNoData.setMessage(
                "smartphones have not yet been posted.you might become the first one to post if you do posting on smartphones" + "\nan opportunity huh? grab that opportunity!\ntry using the search for better results!"
            )
            alertNoData.setNeutralButton("ok") { dialog, _ ->

                //dismiss the dialog
                dialog.dismiss()
                //
            }
            alertNoData.create()
            alertNoData.show()
            //

        } else if (tempArrayListSmartPhones.isNotEmpty()) {
            //list no empty update the recycler with a new adapter of the smartphones
            //null the current adapter of the recycler
            recyclerViewProducts.adapter = null
            //
            val adapterSmartPhones = MyAdapterProducts(tempArrayListSmartPhones, requireActivity())
            //set the adapter as the new recycler adapter
            recyclerViewProducts.adapter = adapterSmartPhones
            //set layoutMgr for recycler
            recyclerViewProducts.layoutManager = LinearLayoutManager(requireActivity())
            //notify the adapter of change in data
            adapterSmartPhones.notifyDataSetChanged()
            //
        }
        //code ends
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    private fun funSortByPowerBanks(selectedCategoryType: String) {
        //code begins
        val tempArrayListPowerBanks = arrayListOf<DataClassProductsData>(
        )
        tempArrayListPowerBanks.clear()
        //use for@ loop to loop through temp arrayList orig
        tempArrayList.forEach {
            if (it.categoryProduct.lowercase(Locale.getDefault()).contains("powerbanks")) {
                //contains pbs
                tempArrayListPowerBanks.add(it)
                //
            }
        }
        //check if is no empty pbs list and evaluate accordingly
        if (tempArrayListPowerBanks.isEmpty()) {
            //alert empty
            //code begins
            val alertNoData = MaterialAlertDialogBuilder(requireActivity())
            alertNoData.setIcon(R.drawable.ic_search)
            alertNoData.background =
                resources.getDrawable(R.drawable.material_seven, requireActivity().theme)
            alertNoData.setTitle("$selectedCategoryType Results")
            alertNoData.setMessage(
                "powerbanks have not yet been posted.you might become the first one to post if you do posting on powerbanks" + "\nan opportunity huh? grab that opportunity!\n" + "try using the search for better results!"
            )
            alertNoData.create()
            alertNoData.setCancelable(false)
            alertNoData.setNeutralButton("wow") { dialog, _ ->
                //dismiss dg to avoid RT exceptions
                dialog.dismiss()
                //
            }
            alertNoData.show()
            //code end
            //
        } else if (tempArrayListPowerBanks.isNotEmpty()) {
            //contains pbs items update the recycler with a new adapter that is of pbs
            //remove the current adapter recycler to null n order to set it with a newer one
            recyclerViewProducts.adapter = null
            //create new adapter for powerbanks
            val adapterPowerBanks = MyAdapterProducts(tempArrayListPowerBanks, requireActivity())
            //assign the adapter to the recycler and notify data changed
            recyclerViewProducts.adapter = adapterPowerBanks
            //set layoutMgr
            recyclerViewProducts.layoutManager = LinearLayoutManager(requireActivity())
            //notify data set changed on the adapter pbs
            adapterPowerBanks.notifyDataSetChanged()

            //
        }

        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun functionSortByUniversity() {
        //code begins
        //alert dg with universities for selecting from
        var universitySelected = ""
        val universitySortAlert = MaterialAlertDialogBuilder(requireActivity())
        universitySortAlert.background =
            resources.getDrawable(R.drawable.material_ten, requireActivity().theme)
        universitySortAlert.setTitle("Select University")
        universitySortAlert.setCancelable(false)
        universitySortAlert.setSingleChoiceItems(universitiesForSort, 0) { _, which ->
            universitySelected = universitiesForSort[which]

            //toast which university has been selected
            Toasty.custom(
                requireActivity(),
                universitySelected,
                R.drawable.ic_nike_done,
                R.color.colorWhite,
                Toasty.LENGTH_SHORT,
                true,
                false
            ).show()
            //
        }
        universitySortAlert.setIcon(R.drawable.ic_cart)
        universitySortAlert.setPositiveButton("sort Now") { dialog, _ ->

            if (universitySelected.isNotEmpty()) {

                //call function that will populate the adapter with list of universities selected
                funModifyAdapterRecyclerByUni(universitySelected)
                //

            } else if (universitySelected.isEmpty()) {

                //toast select a university and dismiss dg to avoid RT Exceptions
                Toasty.custom(
                    requireActivity(),
                    "hey, select a university",
                    R.drawable.ic_smile,
                    R.color.colorWhite,
                    Toasty.LENGTH_SHORT,
                    true,
                    false
                ).show()
                //
            }

            //dismiss dialog
            dialog.dismiss()
            //

        }
        //delay using handler for create and show
        Handler(Looper.getMainLooper()).postDelayed({
            //delay  1sec display Alert Universities
            universitySortAlert.create()
            universitySortAlert.show()
        }, 500)
        //code ends
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funModifyAdapterRecyclerByUni(universitySelected: String) {
        //code begins
        //create a new arrayList which will be populated by the data of the original array and the temNeArray
        //be used in the adapter of the recyclerView to populated the data. if no data of the selected uni is
        //the case, revert back by copying the data of the original list into the temp list used by the adapter
        var tempArrayListUniversitySelected = arrayListOf<DataClassProductsData>()
        //first clear the arraylist the copy the data/replicate from original arraylist
        //
        //lowercase selected uni and also the unis of the original array for comparison ignore case sensitivity
        var lowercaseUniSelected = universitySelected.lowercase(Locale.getDefault())
        //check if is not empty lowerCaseSelected uni
        if (lowercaseUniSelected.isNotEmpty()) {
            //use for @loop on the original list and add the data of the university into the new temp array
            arrayListProducts.forEach {

                if (it.vicinityProduct.lowercase(
                        Locale.getDefault()
                    ).contains(lowercaseUniSelected)
                ) {
                    //assign the it data into the arrayTemp
                    tempArrayListUniversitySelected.add(it)
                    //
                }
            }
            //check if the tempList is empty. if so start the home
            if (tempArrayListUniversitySelected.isEmpty()) {
                //alert dismissible  to the user no data
                funAlertNoData(universitySelected)
                //
            } else if (tempArrayListUniversitySelected.isNotEmpty()) {
                //set the adapter of the recycler view into another of support university sort and notify adapter change
                recyclerViewProducts.adapter = null
                adapterRecycler.notifyDataSetChanged()
                //init of new adapter
                val adapterUniversitySort =
                    MyAdapterProducts(tempArrayListUniversitySelected, requireActivity())
                //setting the new adapter to the recycler view
                recyclerViewProducts.adapter = adapterUniversitySort
                //setting the linearLayout for the adapter then notify adapter data change
                recyclerViewProducts.layoutManager = LinearLayoutManager(requireActivity())
                adapterUniversitySort.notifyDataSetChanged()
                //
            }
        }


        //code ends

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funAlertNoData(universitySelected: String) {

        //code begins
        val alertNoData = MaterialAlertDialogBuilder(requireActivity())
        alertNoData.setIcon(R.drawable.ic_search)
        alertNoData.setTitle("No Product Results")
        alertNoData.setCancelable(false)
        alertNoData.background =
            resources.getDrawable(R.drawable.material_ten, requireActivity().theme)

        alertNoData.setMessage(
            "comrades at $universitySelected have not yet posted any product.\nthey might be unaware of this free online marketing application.\nenlighten them  by sharing the application to a friend thence.\ntry using the search for better results!"
        )
        alertNoData.setNeutralButton("ok") { dialog, _ ->

            //dismiss the dialog
            dialog.dismiss()
            //
        }
        alertNoData.create()
        alertNoData.show()
        //code end
    }


    private fun updateTitle(title: String) {
        requireActivity().title = title
    }


}