package com.shimitadouglas.marketcm.fragmentProducts

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.adapter_products_posted.MyAdapterProducts
import com.shimitadouglas.marketcm.mains.ProductsHome
import com.shimitadouglas.marketcm.modal_data_posts.DataClassProductsData
import com.shimitadouglas.marketcm.modal_data_slide_model.DataClassSlideModal
import com.shimitadouglas.marketcm.modal_sheets.ModalPostProducts.Companion.CollectionPost
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

class HomeFragment : Fragment() {
    //string for holding selected uni for sorting products
    private var selected: String = ""

    //
    private val TAG = "HomeFragment"

    //init of the global
    private lateinit var viewHome: View
    lateinit var toolbarHome: Toolbar

    //late init var recyclerViewHome: RecyclerView
    private lateinit var collapsingToolbarLayoutHome: CollapsingToolbarLayout
    private lateinit var appBarLayoutHome: AppBarLayout
    private lateinit var floatingActionButtonHome: FloatingActionButton
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var imageSlider: ImageSlider
    private lateinit var viewProgression: View
    private lateinit var progressDialogMain: ProgressDialog
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
    //arrayList Universities

    //init of globals
    private var universitiesForSort = arrayOf<String>()
    //


    @OptIn(DelicateCoroutinesApi::class)
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
        floatingActionButtonHome.setOnClickListener {
            funFabClicked()
        }

        //slide models run to run in the background
        funArrayListSlideModels()
        //

        //init recycler
        funRecyclerOperationsAndPostDataLoading()
        //

        //fun toolbarOperations
        funToolbarOperations()

        //code ends
        return viewHome
    }


    private fun funFabClicked() {
        //code begins
        floatingActionButtonHome.apply {
            startAnimation(AnimationUtils.loadAnimation(requireActivity(), R.anim.rotate_avg))
            postDelayed({
                //restart the whole home products to reload
                val intentRefreshHome = Intent(requireActivity(), ProductsHome::class.java)
                intentRefreshHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intentRefreshHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                requireActivity().startActivity(intentRefreshHome)
                //
            }, 1000)
        }
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
                    //do no show alertSearchHintDialog instead direct search
                    funSearchProducts()
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

    @SuppressLint("NotifyDataSetChanged")
    private fun funRecyclerOperationsAndPostDataLoading() {
        //code begins
        //fetch the data from the cloud on the api of the post
        //sort the products in relation to date posted
        val postsApiStore = FirebaseFirestore.getInstance()
        postsApiStore.collection(CollectionPost).get().addOnSuccessListener {
            if (!it.isEmpty) {
                //disable pgD
                progressDialogMain.dismiss()
                //

                // data present
                //init products arrayList main
                arrayListProducts = arrayListOf<DataClassProductsData>()
                //
                for (post in it.documents) {
                    val dataPosts: DataClassProductsData? =
                        post.toObject(DataClassProductsData::class.java)
                    if (dataPosts != null) {
                        arrayListProducts.add(dataPosts)
                    }
                }

                //creating another tempArraylist say arrayListProductsSortedLatestFirst that will contain latest items first while old items
                //last by help of the while looping strategy with decrement system where the latest element becomes first in the new array
                //
                var arrayListProductsSortedLatestFirst = arrayListOf<DataClassProductsData>()
                var maxSizeArrayListProductsFetched = arrayListProducts.size - 1
                while (maxSizeArrayListProductsFetched >= 0) {
                    arrayListProductsSortedLatestFirst.add(arrayListProducts[maxSizeArrayListProductsFetched])
                    maxSizeArrayListProductsFetched--
                }
                //
                //placing the changes to the recycler view with help of tempArraylist that will ease flexibility in times of searching
                //init the tempArrayList
                tempArrayList = arrayListOf<DataClassProductsData>()
                //

                //add all the data of arrayListSortedFirst into tempArrayList /replicate it into the tempArrayList
                tempArrayList.addAll(arrayListProductsSortedLatestFirst)
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

            } else if (it.isEmpty) {
                //no data is present
                progressDialogMain.dismiss()
                //
                funToastyFail("server not responding")
                return@addOnSuccessListener
                //
            }
        }.addOnFailureListener {
            //dismisses
            progressDialogMain.dismiss()
            //
            //toast error
            funToastyFail("error was encountered while fetching data")
            AlertDialog.Builder(requireActivity())
                .setMessage(it.message)
                .create().show()

            //
        }
        //
        //code ends

    }

    private fun funArrayListSlideModels() {
        val arraylistHoldSlideModelImagePath = arrayListOf<String>()
        val arrayListHoldSlideModelTitles = arrayListOf<String>()
        val arraylistHoldSlideModels = arrayListOf<SlideModel>()
        //code begins
        //fetch the data from store(public-Repo) and deal wit only imageUri and tittle
        val storePublicRepo = FirebaseFirestore.getInstance()
        storePublicRepo.collection(CollectionPost).get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (data in it.result.documents) {
                    //filter the data
                    val classData: DataClassSlideModal? =
                        data.toObject(DataClassSlideModal::class.java)
                    //add top max 10 only else return
                    //add images in the arrayListImages
                    val imagePath = classData?.imageProduct
                    if (imagePath != null) {
                        if (arraylistHoldSlideModelImagePath.size <= 10) {
                            arraylistHoldSlideModelImagePath.add(imagePath)
                        } else {
                            //size of the images is exceeding 10
                            return@addOnCompleteListener
                        }
                    }
                    //
                    //lets hold a max of only ten images in the array
                    //lets display the latest top 10 posts of the day
                    //add title into the arrayListTitle
                    val title = classData?.title
                    if (title != null) {
                        if (arrayListHoldSlideModelTitles.size <= 10) {
                            arrayListHoldSlideModelTitles.add(title)
                        } else {
                            //the size of the array is greater than ten hence no need to add more only top 10 are enough
                            return@addOnCompleteListener
                        }
                    }
                    //

                    //log data to see
                    Log.d(TAG, "funArrayListSlideModels: image:$imagePath\ntitle:$title")
                    //
                }

                //here add the data onto the arrayListSlideModal
                if (arrayListHoldSlideModelTitles.isNotEmpty() and (arraylistHoldSlideModelImagePath.isNotEmpty())) {
                    Log.d(
                        TAG,
                        "funArrayListSlideModels: size array images:${arraylistHoldSlideModelImagePath.size}" +
                                "\nsize array title:${arrayListHoldSlideModelTitles.size}"
                    )

                    //display the images in reversed order==latest at the top while old at the bottom
                    //define the strategy decremental from the max array to the least @ -1 none is having an item

                    //can use array images or arrayTitles for the array max size since are equal both
                    var arrayMaxSize = arrayListHoldSlideModelTitles.size - 1
                    while (arrayMaxSize >= 0) {
                        arraylistHoldSlideModels.add(
                            SlideModel(
                                arraylistHoldSlideModelImagePath[arrayMaxSize],
                                arrayListHoldSlideModelTitles[arrayMaxSize]
                            )
                        )
                        arrayMaxSize--
                    }
                    //add the slide models into its corresponding arraylist
                    imageSlider.setImageList(arraylistHoldSlideModels, ScaleTypes.CENTER_CROP)
                    //
                }
                //
            }
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
                collapsingToolbarLayoutHome.title = "trending now"
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

    @SuppressLint("PrivateResource", "InflateParams")
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

        //init the arrayUniversity from the resource arrayStrings and sort em
        universitiesForSort = resources.getStringArray(R.array.universities_ke)
        universitiesForSort.sort()
        //

        //initialise the view progression w/c is used in place of progressD will be shown in an alertD when data is no fetched from
        //the store public repo
        viewProgression = LayoutInflater.from(requireActivity())
            .inflate(R.layout.general_progress_dialog_view, null, false)
        //

        //init the material alert dialog
        progressDialogMain = ProgressDialog(requireActivity())
        progressDialogMain.apply {
            setTitle("Products Data")
            setView(viewProgression)
            setCancelable(false)
            setMessage("Loading")
            setIcon(R.drawable.ic_download)
            create()
            show()
        }
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

        //
        funToastyCustomTwo(
            "search",
            R.drawable.ic_search,
            R.color.androidx_core_secondary_text_default_material_light
        )
        //

        //creating a menu item by id from toolbar
        val menu = this.toolbarHome.menu
        val menuItem: MenuItem = menu.findItem(R.id.searchProduct)
        //creating a searchView
        val searchView: SearchView = menuItem.actionView as SearchView
        searchView.queryHint = getString(R.string.type_here_to_search)
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
                        if (it.title?.lowercase(Locale.getDefault())
                                ?.contains(lowerCaseNewText) == true
                        ) {
                            //search contains data of title thus add it into the tempArrayList for display
                            //change the background color of it to show the user where search was found then add to tempArrayList

                            tempArrayList.add(it)
                            //

                        } else if (it.Owner?.lowercase(Locale.getDefault())
                                ?.contains(lowerCaseNewText) == true
                        ) {
                            //search contains owner of the product. add it into the tempArrayList for display
                            tempArrayList.add(it)
                            //

                        } else if (it.productID?.lowercase(Locale.getDefault())
                                ?.contains(lowerCaseNewText) == true
                        ) {
                            //search contains product id. add it into the tempArrayList for display
                            tempArrayList.add(it)
                            //
                        } else if (it.university?.lowercase(Locale.getDefault())
                                ?.contains(lowerCaseNewText) == true
                        ) {
                            //search contains vicinity/place of the product. add it into the tempArrayList for display
                            tempArrayList.add(it)
                            //
                        } else if (it.description?.lowercase(Locale.getDefault())
                                ?.contains(lowerCaseNewText) == true
                        ) {
                            //search contains description. add it into the temp ArrayList for display
                            tempArrayList.add(it)
                            //
                        } else if (it.category?.lowercase(Locale.getDefault())
                                ?.contains(lowerCaseNewText) == true
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
        //create list that loads data from stringArray Resource
        var listSortOptions: Array<String> = resources.getStringArray(R.array.listSortOptions)
        listSortOptions.sort()
        //
        val alertSortMethod = MaterialAlertDialogBuilder(requireActivity())
        alertSortMethod.setTitle("sort by")
        alertSortMethod.setCancelable(false)
        alertSortMethod.setIcon(R.drawable.ic_sort)
        alertSortMethod.background =
            resources.getDrawable(R.drawable.material_16, requireActivity().theme)
        alertSortMethod.setSingleChoiceItems(listSortOptions, 8) { _, which ->
            //save the sorting option in a variable selected,be used for other evaluations
            selected = listSortOptions[which]
            //toast which sort method is selected
            funToastyShow(selected)
            //
        }
        alertSortMethod.setPositiveButton("sort") { _, _ ->

            //code begins
            //check if empty is no selection before sorting
            if (selected.isNotEmpty()) {
                //check if university is the selection
                if (selected.contains("university")) {
                    //call function display University Names
                    //toast fun
                    funToastyShow("university sort")
                    //
                    functionSortByUniversity()
                    //
                } else if (selected.contains("category", true)) {
                    //fun toast
                    funToastyShow("category sort")
                    //call alert show sort option by category
                    funSortByCategory()
                    //
                } else if (selected.contains("latest first", true)) {

                    //revert back to home since it's the default to latest first
                    funSortByTimeLatestDefault()
                    //
                } else if (selected.contains("old first", true)) {
                    //call function to load the original array list which on launch only contains
                    funSortOldFirstUsingOriginalArrayList()
                    //
                } else if (selected.contains("owners names(a-z)", true)) {
                    //call fun to sort names in asc order
                    val field = "Owner"
                    val orderStyle = Query.Direction.ASCENDING
                    funSortDetailsFetchedAsRequired(field, orderStyle)
                    //
                } else if (selected.contains("owners names(z-a)", true)) {
                    val field = "Owner"
                    val orderStyle = Query.Direction.DESCENDING
                    funSortDetailsFetchedAsRequired(field, orderStyle)

                } else if (selected.contains("products names(a-z)", true)) {
                    val field = "title"
                    val orderStyle = Query.Direction.ASCENDING
                    funSortDetailsFetchedAsRequired(field, orderStyle)

                } else if (selected.contains("products names(z-a)", true)) {
                    val field = "title"
                    val orderStyle = Query.Direction.DESCENDING
                    funSortDetailsFetchedAsRequired(field, orderStyle)
                }
            } else if (selected.isEmpty()) {
                //show user must select a university
                funToastyShow("hey, select a sorting method")
                //
            }
            //code ends
        }
        alertSortMethod.create()
        alertSortMethod.show()
        //

        //code ends
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funSortDetailsFetchedAsRequired(s: String, orderStyle: Query.Direction) {
        //use order by function in store fetching data to filter the results as per the parameter
        //pass the parameter of field and direction and let the magic happens
        val store = FirebaseFirestore.getInstance().collection(CollectionPost)
        store.orderBy(s, orderStyle).get().addOnCompleteListener {
            if (it.isSuccessful) {
                //data loaded successfully
                //create an array that will always temporarily store the data filtered by the class
                val arrayListSortData = arrayListOf<DataClassProductsData>()
                //clear the list
                arrayListSortData.clear()
                //

                for (data in it.result.documents) {
                    val classDataFilter: DataClassProductsData? =
                        data.toObject(DataClassProductsData::class.java)
                    if (classDataFilter != null) {
                        arrayListSortData.add(classDataFilter)
                    } else {
                        funToastyFail("something went wrong!")
                    }
                }
                //check if the new array is empty or not
                if (arrayListSortData.isEmpty()) {
                    funToastyFail("error encountered")
                } else if (arrayListSortData.isNotEmpty()) {
                    val adapterSortedNamesAsc =
                        MyAdapterProducts(arrayListSortData, requireActivity())
                    recyclerViewProducts.adapter = null
                    recyclerViewProducts.apply {
                        adapter = adapterSortedNamesAsc
                        layoutManager = LinearLayoutManager(requireActivity())
                        funToastyShow("sorted successfully")
                    }
                }
                //
            } else if (!it.isSuccessful) {
                //data fetching from the store was no successful
                funToastyFail("sorting failed!")
                //
            }
        }
        //
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funSortOldFirstUsingOriginalArrayList() {
        //code begins
        //check if the old arrayList is empty or not. if empty fail the process
        if (arrayListProducts.isEmpty()) {
            funToastyFail("sort failed!")
        } else if (arrayListProducts.isNotEmpty()) {
            //there is data in the array lets initiate the process of loading this data onto the recycler view
            //no intensive logic is required since the original array list loads data in response to old first latest below
            //old
            val adapterLoadOldArrayList = MyAdapterProducts(arrayListProducts, requireActivity())
            //assign the adapter on to the rv
            recyclerViewProducts.apply {
                adapter = null
                adapter = adapterLoadOldArrayList
                layoutManager = LinearLayoutManager(requireActivity())
                adapterLoadOldArrayList.notifyDataSetChanged()
                funToastyShow("sort successful")
            }
        } else {
            //sth else is wrong
            funToastyFail("something went wrong")
            //
        }
        //code ends
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

    private fun funSortByTimeLatestDefault() {
        //code begins
        //recreate the activity since it is sorted by time
        val intent = Intent(requireActivity(), ProductsHome::class.java)
        startActivity(intent)
        //
        funToastyShow("sorted successfully")
        //code ends
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funSortByCategory() {
        //code begins
        //list that contains the methods od category sort
        var listCategorySort = arrayOf<String>()
        listCategorySort = resources.getStringArray(R.array.list_category_sort)
        //sort the list
        listCategorySort.sort()
        //
        var selectedCategoryType = ""

        val alertSortByCategory = MaterialAlertDialogBuilder(requireActivity())
        alertSortByCategory.setTitle("select category")
        alertSortByCategory.setIcon(R.drawable.ic_sort)
        alertSortByCategory.background =
            resources.getDrawable(R.drawable.general_alert_dg, requireActivity().theme)
        alertSortByCategory.setSingleChoiceItems(listCategorySort, 12) { _, which ->
            selectedCategoryType = listCategorySort[which]
            //

            //toast to the user which sort category he/she opted for
            funToastyShow(selectedCategoryType)
            //
        }
        alertSortByCategory.setNeutralButton("sortNow") { dialog, _ ->

            //code here the impact of the selected category Type
            if (selectedCategoryType.isEmpty()) {
                //code begins
                funToastyShow("hey,select a category")
                //dismiss
                dialog.dismiss()
                //

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
                    //dismiss
                    dialog.dismiss()
                    //

                } else if (selectedCategoryType.lowercase(
                        Locale.getDefault()
                    ).contains("smartphones")
                ) {
                    //call function sort by smartphones
                    funSortBySmartPhones(selectedCategoryType)
                    //

                    //dismiss
                    dialog.dismiss()
                    //

                } else if (selectedCategoryType.lowercase(Locale.getDefault()).contains(
                        "tablets"
                    ) || selectedCategoryType.lowercase(Locale.getDefault()).contains("ipads")
                ) {
                    //call function sort by tablets/ipads
                    funSortByTabletsIpads(selectedCategoryType)
                    //
                    //dismiss
                    dialog.dismiss()
                    //
                } else if (selectedCategoryType.lowercase(Locale.getDefault())
                        .contains("laptops") || selectedCategoryType.lowercase(Locale.getDefault())
                        .contains("desktops")
                ) {
                    //call fun sort by laptops
                    funSortByLaptopDesktops(selectedCategoryType)
                    //

                    //dismiss
                    dialog.dismiss()
                    //

                } else if (selectedCategoryType.lowercase(Locale.getDefault())
                        .contains("tvs") || selectedCategoryType.lowercase(
                        Locale.getDefault()
                    ).contains("flatscreens")
                ) {
                    //fun sort by tvs.flatscreens
                    funSortByTvFlatScreen(selectedCategoryType)
                    //

                    //dismiss
                    dialog.dismiss()
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

                    //dismiss
                    dialog.dismiss()
                    //

                } else if (selectedCategoryType.lowercase(Locale.getDefault())
                        .contains("earphones") || selectedCategoryType.lowercase(
                        Locale.getDefault()
                    ).contains("headphones")
                ) {
                    //fun sort by earphones headphones
                    funSortByEarphoneHeadPhones(selectedCategoryType)
                    //

                    //dismiss
                    dialog.dismiss()
                    //

                } else if (selectedCategoryType.lowercase(Locale.getDefault())
                        .contains("woofer") || selectedCategoryType.lowercase(
                        Locale.getDefault()
                    ).contains("system") || selected.lowercase(Locale.getDefault()).contains("bt")
                ) {

                    //fun sort by woofer system robot BT
                    funSortByWooferSystemRobotBT(selectedCategoryType)
                    //

                    //dismiss
                    dialog.dismiss()
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

                    //dismiss
                    dialog.dismiss()
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
                    //dismiss
                    dialog.dismiss()
                    //

                } else if (selectedCategoryType.lowercase(Locale.getDefault())
                        .contains("hard") || selectedCategoryType.contains("ssd")
                ) {
                    Toast.makeText(requireActivity(), "hard disks/ssds", Toast.LENGTH_SHORT).show()

                    //fun sort by HDD or SSDs
                    funSortByHDDSSD(selectedCategoryType)
                    //

                    //dismiss
                    dialog.dismiss()
                    //
                } else if (selectedCategoryType.lowercase(Locale.getDefault()).contains("ram")) {
                    Toast.makeText(requireActivity(), "Laptop RAMS", Toast.LENGTH_SHORT).show()
                    //call function sort items by RAMS
                    funSortBYRAM(selectedCategoryType)
                    //

                    //dismiss
                    dialog.dismiss()
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
            if (it.category?.lowercase(Locale.getDefault())?.contains("ram") == true) {
                tempArrayRAMS.add(it)
            }
        }

        if (tempArrayRAMS.isEmpty()) {
            //call fun no results
            funNoResultsCategorySort(selectedCategoryType)
            //
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
            if (it.category?.lowercase(Locale.getDefault())
                    ?.contains("SSD") == true
            ) {
                tempArrayListHDDSSD.add(it)
            }
        }
        if (tempArrayListHDDSSD.isEmpty()) {
            //call fun no results on the sort by category
            funNoResultsCategorySort(selectedCategoryType)
            //
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
            if (it.category?.lowercase(Locale.getDefault())
                    ?.contains("flash") == true
            ) {
                tempArrayListFlashSD.add(it)
            }
        }

        if (tempArrayListFlashSD.isEmpty()) {
            //call fun no results
            funNoResultsCategorySort(selectedCategoryType)
            //
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

            if (it.category?.lowercase(Locale.getDefault())
                    ?.contains("kabambe") == true
            ) {
                tempArrayListKabambeKaduda.add(it)
            }
        }
        if (tempArrayListKabambeKaduda.isEmpty()) {
            //call fun no results
            funNoResultsCategorySort(selectedCategoryType)
            //
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
            if (it.category?.lowercase(Locale.getDefault())
                    ?.contains("woofer") == true
            ) {
                tempArrayListWooferSystem.add(it)
            }

        }
        if (tempArrayListWooferSystem.isEmpty()) {
            //call fun no results
            funNoResultsCategorySort(selectedCategoryType)
            //
        }

        //code ends
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    private fun funSortByEarphoneHeadPhones(selectedCategoryType: String) {

        //code begins
        val tempArrayListEarphonesHeadPhones = arrayListOf<DataClassProductsData>()
        tempArrayListEarphonesHeadPhones.clear()
        tempArrayList.forEach {

            if (it.category?.lowercase(Locale.getDefault())
                    ?.contains("earphones") == true
            ) {
                tempArrayListEarphonesHeadPhones.add(it)
            }
        }
        if (tempArrayListEarphonesHeadPhones.isEmpty()) {
            //call fun no results
            funNoResultsCategorySort(selectedCategoryType)
            //
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
            if (it.category?.lowercase(Locale.getDefault())
                    ?.contains("shoes") == true
            ) {
                tempArrayListShoesDripsDior.add(it)
            }
        }
        if (tempArrayListShoesDripsDior.isEmpty()) {
            //call fun no results
            funNoResultsCategorySort(selectedCategoryType)
            //
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
            if (it.category?.lowercase(Locale.getDefault())
                    ?.contains("tvs") == true
            ) {
                tempArrayListTvsFlats.add(it)
            }
        }
        if (tempArrayListTvsFlats.isEmpty()) {
            //call fun no results
            funNoResultsCategorySort(selectedCategoryType)
            //
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
            if (it.category?.lowercase(Locale.getDefault())
                    ?.contains("laptop") == true
            ) {
                tempArrayListLaptopDeskTops.add(it)
            }
        }
        if (tempArrayListLaptopDeskTops.isEmpty()) {
            //call fun no results
            funNoResultsCategorySort(selectedCategoryType)
            //
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
            if (it.category?.lowercase(Locale.getDefault())
                    ?.contains("tablets") == true
            ) {
                tempArrayListTabletsIpads.add(it)
            }
        }
        if (tempArrayListTabletsIpads.isEmpty()) {
            //call fun no results
            funNoResultsCategorySort(selectedCategoryType)
            //
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

            if (it.category?.lowercase(Locale.getDefault())?.contains("smartphones") == true) {
                //smartphones data present
                tempArrayListSmartPhones.add(it)
                //
            }
        }

        //check if empty tempArrayList Products and react accordingly
        if (tempArrayListSmartPhones.isEmpty()) {
            //call fun no results
            funNoResultsCategorySort(selectedCategoryType)
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
            if (it.category?.lowercase(Locale.getDefault())?.contains("powerbanks") == true) {
                //contains pbs
                tempArrayListPowerBanks.add(it)
                //
            }
        }
        //check if is no empty pbs list and evaluate accordingly
        if (tempArrayListPowerBanks.isEmpty()) {
            //call fun no results
            funNoResultsCategorySort(selectedCategoryType)
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
            funToastyShow(universitySelected)
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
                funToastyCustomTwo(
                    "hey select a university",
                    R.drawable.ic_smile,
                    R.color.androidx_core_secondary_text_default_material_light
                )
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
        //first clear the arraylist then copy the data/replicate from original arraylist
        //
        //lowercase selected uni and also the unis of the original array for comparison ignore case sensitivity
        var lowercaseUniSelected = universitySelected.lowercase(Locale.getDefault())
        //check if is not empty lowerCaseSelected uni
        if (lowercaseUniSelected.isNotEmpty()) {
            //use for @loop on the original list and add the data of the university into the new temp array
            arrayListProducts.forEach {

                if (it.university?.lowercase(
                        Locale.getDefault()
                    )?.contains(lowercaseUniSelected) == true
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
        alertNoData.setTitle("no records")
        alertNoData.setCancelable(false)
        alertNoData.background =
            resources.getDrawable(R.drawable.material_seven, requireActivity().theme)

        alertNoData.setMessage(
            "comrades at $universitySelected have not yet posted any product\n\n" +
                    "they might be unaware of this free online marketing application\n\n" +
                    "enlighten them  by sharing the application to a friend thence\n\n" +
                    "try using the search for better results!"
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


    //fun that creates an alert for showing the results no found for products category
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funNoResultsCategorySort(selectedCategoryString: String): Unit {
        val alertNoData = MaterialAlertDialogBuilder(requireActivity())
        alertNoData.setIcon(R.drawable.ic_sort)
        alertNoData.setTitle(selectedCategoryString)
        alertNoData.setCancelable(false)
        alertNoData.background =
            resources.getDrawable(R.drawable.material_seven, requireActivity().theme)
        alertNoData.setMessage(
            "$selectedCategoryString have not yet been posted \n" +
                    "\ntry using the search for better results!"
        )
        alertNoData.setNeutralButton("ok") { dialog, _ ->

            //dismiss the dialog
            dialog.dismiss()
            //
        }
        alertNoData.create()
        alertNoData.show()
    }

    private fun updateTitle(title: String) {
        requireActivity().title = title
    }


}