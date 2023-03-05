package com.swapnilk.truelink.ui.user_profile

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.network.okHttpClient
import com.example.GetProfileUploadURLQuery
import com.example.GetUserQuery
import com.example.UpdateUserMutation
import com.example.type.Gender
import com.example.type.UpdateUserInput
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.ozcanalasalvar.library.view.popup.DatePickerPopup
import com.squareup.picasso.Picasso
import com.ss.profilepercentageview.ProfilePercentageView
import com.swapnilk.truelink.R
import com.swapnilk.truelink.data.online.AuthorizationInterceptor
import com.swapnilk.truelink.data.online.adapters.FavouriteBrowserAdapter
import com.swapnilk.truelink.databinding.FragmentUpdateUserProfileBinding
import com.swapnilk.truelink.utils.CommonFunctions
import com.swapnilk.truelink.utils.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.create
import pl.droidsonroids.gif.GifImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext


open class UpdateUserProfile : Fragment(), CoroutineScope, SettingsChangedInterface {
    ////////////Start Coroutine for Background Task../////////////
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var _binding: FragmentUpdateUserProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var apolloClient: ApolloClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var commonFunctions: CommonFunctions

    private lateinit var btnEdit: TextView
    private lateinit var btnHome: TextView
    private lateinit var btnSave: TextView

    private lateinit var textProgress: TextView
    private lateinit var ppvProfile: ProfilePercentageView
    private lateinit var editName: TextInputEditText
    private lateinit var editPhone: TextInputEditText
    private lateinit var editAddress: TextInputEditText
    private lateinit var editBirthday: TextInputEditText
    private lateinit var textLayoutBirthday: TextInputLayout
    private lateinit var editGender: AutoCompleteTextView
    private lateinit var progressBar: ProgressBar
    private lateinit var datePickerPopup: DatePickerPopup
    private val calendar = Calendar.getInstance()
    var gender: Gender? = null
    var dob: String = ""
    var name: String = ""
    var address: String = ""
    var dateOfBirth: Long? = null

    var myBitmap: Bitmap? = null
    var picUri: Uri? = null
    private lateinit var tvFavouriteBrowser: TextView


    private var permissionsToRequest: ArrayList<String>? = null
    private val permissionsRejected: ArrayList<String> = ArrayList()
    private val permissions: ArrayList<String> = ArrayList()

    private val ALL_PERMISSIONS_RESULT = 107

    companion object {
        var favouriteBrowser: MutableList<String> = ArrayList()
        lateinit var mListener: SettingsChangedInterface
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateUserProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mListener = this
        showToolBar()
        ///////////////////////////////Initialize UI/////////////////////
        Initialize()
        ////////////////////////////////GET shared Preferences///////////
        sharedPreferences = SharedPreferences(requireActivity())
        commonFunctions = CommonFunctions(requireContext())
        loadSettings()

        ////////////////////////////////////////////////////////////////
        if (commonFunctions.checkConnection(requireContext())) {
            //////////////////////////////Get Apollo Client//////////////////
            try {
                ///////////////////////Initialize ApolloClient////////////////////////////
                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(AuthorizationInterceptor(requireContext().applicationContext))
                    .build()
                apolloClient =
                    ApolloClient.Builder().serverUrl(commonFunctions.getServerUrl())
                        .okHttpClient(okHttpClient).build()

            } catch (e: Exception) {
                e.stackTrace
            }
            getUserDetails()
        } else commonFunctions.showErrorSnackBar(
            requireContext(), ppvProfile, getString(R.string.no_internet), true
        )
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadSettings() {
        if (sharedPreferences.getFavouriteBrowser() != "") {
            tvFavouriteBrowser.text = commonFunctions.getAppNameFromPackageName(
                sharedPreferences.getFavouriteBrowser(),
                requireContext()
            )
            sharedPreferences.getFavouriteBrowser()?.let { favouriteBrowser.add(it) }
        }

        tvFavouriteBrowser.setOnClickListener {
            if (getAllBrowsers().isNotEmpty())
                showPopupWindow(
                    requireContext(),
                    getString(R.string.set_browser),
                    "",
                    "",
                    getString(R.string.done),
                    R.drawable.ic_favourite_browser,
                    "overlay",
                    getAllBrowsers()
                )
            else
                commonFunctions.showToast(
                    requireContext(),
                    getString(R.string.no_browser_found)
                )
        }

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.O)
    fun showPopupWindow(
        context: Context,
        title: String,
        infoText: String,
        infoText1: String,
        buttonText: String,
        iconRes: Int,
        action: String,
        allBrowsers: List<ResolveInfo>,

        ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_popup)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val rvAppList = dialog.findViewById(R.id.rv_app_list) as RecyclerView
        val gifLoader = dialog.findViewById(R.id.iv_gif) as GifImageView
        val icon = dialog.findViewById(R.id.iv_icon) as ImageView
        val body = dialog.findViewById(R.id.tv_title) as TextView
        val info = dialog.findViewById(R.id.tv_text1) as TextView
        val info1 = dialog.findViewById(R.id.tv_text2) as TextView
        val yesBtn = dialog.findViewById(R.id.id_btn_confirm) as Button
        if (!TextUtils.isEmpty(title))
            body.text = title
        if (!TextUtils.isEmpty(infoText))
            info.text = infoText
        if (!TextUtils.isEmpty(infoText1))
            info1.text = infoText
        if (!TextUtils.isEmpty(buttonText))
            yesBtn.text = buttonText
        if (iconRes != 0)
            icon.setImageDrawable(context.getDrawable(iconRes))

        if (action == "overlay") {
            info1.visibility = View.GONE
            gifLoader.visibility = View.GONE
            rvAppList.visibility = View.VISIBLE

            rvAppList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter =
                    FavouriteBrowserAdapter(
                        allBrowsers,
                        requireContext()
                    )
            }
        } else if (action == "notification") {
            info1.visibility = View.GONE
            gifLoader.visibility = View.GONE
        }
        yesBtn.setOnClickListener {
            if (favouriteBrowser.size > 0) {
                dialog.dismiss()
                sharedPreferences.setFavouriteBrowser(favouriteBrowser[0])
                mListener.onSettingChanged(
                    commonFunctions.getAppNameFromPackageName(
                        sharedPreferences.getFavouriteBrowser(),
                        context
                    )
                )
            } else
                commonFunctions.showToast(requireContext(), getString(R.string.select_browser))
        }
        dialog.show()

    }

    //////////////////////Initialize UI/////////////////////////
    private fun Initialize() {
        textProgress = binding.textProgress
        ppvProfile = binding.ppvProfile
        textLayoutBirthday = binding.textLayoutBirthday
        editAddress = binding.editAddress
        editBirthday = binding.editDob
        editName = binding.editName
        editPhone = binding.editPhone
        progressBar = binding.progressBar
        editGender = binding.editGender

        tvFavouriteBrowser = binding.tvFavouriteBrowser

    }

    ////////////////////////////Get User Details Query//////////////////
    private fun getUserDetails() {
        try {
            launch {
                val response: ApolloResponse<GetUserQuery.Data> =
                    apolloClient.query(GetUserQuery()).execute()
                if (response != null) afterResponse(response)
            }
        } catch (e: Exception) {
            e.stackTrace
            commonFunctions.showToast(requireContext(), e.message)

        } catch (e: ApolloException) {
            e.stackTrace
            commonFunctions.showToast(requireContext(), e.message)

        }

    }

    ///////////////////////////Get Image Upload URL/////////////////////
    private fun getProfileUpdateUrl() {
        try {
            launch {
                val response: ApolloResponse<GetProfileUploadURLQuery.Data> =
                    apolloClient.query(GetProfileUploadURLQuery()).execute()
                if (response != null) afterResponseProfileUrl(response)
            }
        } catch (e: Exception) {
            e.stackTrace
            commonFunctions.showToast(requireContext(), e.message)

        } catch (e: ApolloException) {
            e.stackTrace
            commonFunctions.showToast(requireContext(), e.message)

        }
    }

    /////////////////////////////Upload Image Task ///////////////////////////////////////////////
    private fun afterResponseProfileUrl(response: ApolloResponse<GetProfileUploadURLQuery.Data>) {
        if (response.data != null) {
            var url = response.data?.getProfileUploadURL!!.payload.toString()
            if (commonFunctions.checkConnection(requireContext()))
                UploadImageTask(requireContext(), myBitmap!!).execute(url)
            else
                commonFunctions.showErrorSnackBar(
                    requireContext(),
                    ppvProfile,
                    getString(R.string.no_internet),
                    true
                )
        }
    }

    @SuppressLint("SetTextI18n")
    ///////////////////////////////Handle Response and load UI afer get User///////////////////
    private fun afterResponse(response: ApolloResponse<GetUserQuery.Data>) {
        if (response.data != null) {
            progressBar.visibility = View.GONE
            ppvProfile.setValue(25)
            val url = response.data!!.getUser.payload?.avatar.toString()
            if (!TextUtils.isEmpty(url)) {
                /*try {
                    launch {
                        val remoteImageDeferred = async(Dispatchers.IO) {
                            getImageFromUrl(url)

                        }
                        val imageBitmap = remoteImageDeferred.await()
                        //loadImage(imageBitmap)
                        launch(Dispatchers.Default) {
                            //  val filterBitmap = Filter.apply(imageBitmap)
                            withContext(Dispatchers.Main) {
                                loadImage(imageBitmap )
                            }
                            // Log.i(MainActivity.TAG, "Default. Thread: ${Thread.currentThread().name}")
                        }
                    }
                } catch (e: Exception) {
                    e.stackTrace
                    commonFunctions.showToast(requireContext(), e.message)

                } catch (e: ApolloException) {
                    e.stackTrace
                    commonFunctions.showToast(requireContext(), e.message)

                }*/
                Picasso.with(requireContext()).load(url).into(ppvProfile)
            }
            val address = response.data!!.getUser.payload?.address.toString()
            val city = response.data!!.getUser.payload?.city.toString()
            val state = response.data!!.getUser.payload?.state.toString()
            val country = response.data!!.getUser.payload?.country.toString()
            var strAddress = ""
            if (!TextUtils.isEmpty(address) && address != "null") strAddress += address
            if (!TextUtils.isEmpty(city) && city != "null") strAddress = "$strAddress, $city"
            if (!TextUtils.isEmpty(state) && state != "null") strAddress = "$strAddress, $state"
            if (!TextUtils.isEmpty(country) && country != "null") strAddress =
                "$strAddress, $country"
            editAddress.setText(
                strAddress
            )
            editName.setText(response.data!!.getUser.payload?.fullname.toString())

            editPhone.setText(
                response.data!!.getUser.payload?.dialcode.toString() + " " + response.data!!.getUser.payload?.phone.toString()
            )
            if (response.data!!.getUser.payload?.gender != null) {
                val gender = response.data!!.getUser.payload?.gender as Gender
                editGender.setText(gender.rawValue.toString())
            }
            editBirthday.setText(commonFunctions.convertTimeStamp2Date(response.data!!.getUser.payload?.dob.toString()))
        }
    }

    //////////////////////Download Image from URL//////////////////////
    private fun getImageFromUrl(url: String): Bitmap = URL(url).openStream().use {
        BitmapFactory.decodeStream(it)
    }

    ///////////////////////////Load Profile  Photo from to Image View///////////////////////
    private fun loadImage(bmp: Bitmap) {
        ppvProfile.setImageBitmap(bmp)
        ppvProfile.setValue(50)
    }

    //////////////////////////Set Toolbar for User Profile//////////////////////////////
    private fun showToolBar() {
        activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE

        btnHome = binding.toolbarProfile.btnHomeUp
        btnEdit = binding.toolbarProfile.btnEdit
        btnSave = binding.toolbarProfile.btnSave
        btnHome.setOnClickListener {
            activity?.onBackPressed()
        }

        btnEdit.setOnClickListener {
            editUser()
        }/////////////////End Edit OnClick
    }///////////////End Of Function

    ///////////////////////////////////Edit User Details////////////////////
    private fun editUser() {
        editGender.isEnabled = true
        editBirthday.isEnabled = true
        editAddress.isEnabled = true
        editName.isEnabled = true

        val item = arrayOf(
            "Male", "Female", "Other"
        )

        editGender.setAdapter(
            ArrayAdapter<String>(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, item
            )
        )
        editGender.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                editGender.showDropDown()

                return v?.onTouchEvent(event) ?: true
            }
        })

        datePickerPopup = commonFunctions.createDatePickerDialog(requireContext())
        editBirthday.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                datePickerPopup.show()
                return v?.onTouchEvent(event) ?: true
            }

        })
        datePickerPopup?.setListener(DatePickerPopup.OnDateSelectListener { dp, date, day, month, year ->
            val monthName: Int = (month + 1)
            calendar.set(Calendar.MONTH, month)
            val sdf = SimpleDateFormat("MMM")
            var monthStr = sdf.format(calendar.time)
            editBirthday.setText("$monthStr, $day   $year")
            dateOfBirth = commonFunctions.convertDate2TimeStamp("$monthName/$day/$year")
        })

        ppvProfile.setOnClickListener {
            getImageFromChooser()
        }

        btnEdit.visibility = View.GONE
        btnSave.visibility = View.VISIBLE

        btnSave.setOnClickListener {
            if (commonFunctions.checkConnection(requireContext()))
                saveUser()
            else
                commonFunctions.showErrorSnackBar(
                    requireContext(),
                    ppvProfile,
                    getString(R.string.no_internet),
                    true
                )
        }//////////////End Save OnClick
    }

    /////////////////////////Select Image Options//////////////////
    private fun getImageFromChooser() {
        startActivityForResult(getPickImageChooserIntent(), 200)

        permissions.add(android.Manifest.permission.CAMERA)
        permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissionsToRequest = findUnAskedPermissions(permissions)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            var names: Array<String>? = permissionsToRequest?.toTypedArray()
            if (permissionsToRequest?.size!! > 0) requestPermissions(
                names!!, ALL_PERMISSIONS_RESULT
            );
        }
    }

    private fun getCaptureImageOutputUri(): Uri? {
        var outputFileUri: Uri? = null
        val getImage: File? = activity?.externalCacheDir
        if (getImage != null) {
            outputFileUri = Uri.fromFile(File(getImage.path, "profile.png"))
        }
        return outputFileUri
    }

    private fun getPickImageChooserIntent(): Intent? {

        // Determine Uri of camera image to save.
        val outputFileUri: Uri? = getCaptureImageOutputUri()
        val allIntents: MutableList<Intent> = ArrayList()
        val packageManager: PackageManager? = activity?.packageManager

        // collect all camera intents
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam: List<ResolveInfo> =
            packageManager?.queryIntentActivities(captureIntent, 0) as List<ResolveInfo>
        for (res: ResolveInfo in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            }
            allIntents.add(intent)
        }

        // collect all gallery intents
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        val listGallery: List<ResolveInfo> = packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            allIntents.add(intent)
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        var mainIntent: Intent? = allIntents[allIntents.size - 1]
        for (intent in allIntents) {
            if (intent.component!!.className == "com.android.documentsui.DocumentsActivity") {
                mainIntent = intent
                break
            }
        }
        allIntents.remove(mainIntent)

        // Create a chooser from the main intent
        val chooserIntent = Intent.createChooser(mainIntent, "Select source")

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray())
        return chooserIntent
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap? {
        val ei = ExifInterface(selectedImage.path!!)
        return when (ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 0) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun findUnAskedPermissions(wanted: ArrayList<String>): ArrayList<String>? {
        val result = ArrayList<String>()
        for (perm in wanted) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }
        return result
    }

    private fun hasPermission(permission: String): Boolean {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return checkSelfPermission(
                    requireContext(), permission
                ) === PermissionChecker.PERMISSION_GRANTED
            }
        }
        return true
    }

    private fun canMakeSmores(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }

    private fun getPickImageResultUri(data: Intent?): Uri? {
        var isCamera = true
        if (data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera) getCaptureImageOutputUri() else data!!.data
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("pic_uri", picUri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var bitmap: Bitmap? = null
        if (resultCode == Activity.RESULT_OK) {
            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data)
                try {
                    myBitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, picUri)
                    // myBitmap = rotateImageIfRequired(myBitmap!!, picUri!!)
                    //  myBitmap = getResizedBitmap(myBitmap!!, 500)
                    ppvProfile.setImageBitmap(myBitmap!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                if (data != null) {
                    bitmap = (data.extras!!["data"] as Bitmap?)!!
                }
                myBitmap = bitmap
                ppvProfile.setImageBitmap(myBitmap!!)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            ALL_PERMISSIONS_RESULT -> {
                for (perms in permissionsToRequest!!) {
                    if (hasPermission(perms)) {
                    } else {
                        permissionsRejected.add(perms)
                    }
                }
                if (permissionsRejected.size > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected[0])) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                DialogInterface.OnClickListener { dialog, which ->
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                        //Log.d("API123", "permisionrejected " + permissionsRejected.size());
                                        requestPermissions(
                                            permissionsRejected.toTypedArray(),
                                            ALL_PERMISSIONS_RESULT
                                        )
                                    }
                                })
                            return
                        }
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(requireContext()).setMessage(message)
            .setPositiveButton("OK", okListener).setNegativeButton("Cancel", null).create().show()
    }

    //////////////////////////save User Details///////////////////////////////////
    private fun saveUser() {
        name = editName.text.toString()
        dateOfBirth = commonFunctions.convertDate2TimeStamp(editBirthday.text.toString())
        address = editAddress.text.toString()
        gender = if (editGender.text.toString().equals("Male", ignoreCase = true)) Gender.MALE
        else if (editGender.text.toString().equals("Female", ignoreCase = true)) Gender.FEMALE
        else Gender.OTHERS

        if (myBitmap != null && commonFunctions.checkConnection(requireContext())) {
            getProfileUpdateUrl()
        } else
            commonFunctions.showErrorSnackBar(
                requireContext(),
                editName,
                getString(R.string.no_internet),
                true
            )

        ////////////////Add required parameters//////////////////
        val updateUserInput = UpdateUserInput(
            Optional.Present(editName.text.toString()),
            Optional.Absent,
            Optional.Absent,
            Optional.Present(address),
            Optional.Absent,
            Optional.Absent,
            Optional.Absent,
            Optional.Absent,
            Optional.Absent,
            Optional.Present(dateOfBirth),
            Optional.Present(gender)
        )
        ///////////////Initialize mutation/////////////
        val updateUserMutation = UpdateUserMutation(
            updateUserInput

        )
        /////////////////Perform Background Task///////////////////
        try {
            launch {
                progressBar.visibility = View.VISIBLE
                val response: ApolloResponse<UpdateUserMutation.Data> =
                    apolloClient.mutation(updateUserMutation).execute()
                afterResult(response)
            }
        } catch (e: Exception) {
            e.stackTrace
            commonFunctions.showToast(requireContext(), e.message)

        } catch (e: ApolloException) {
            e.stackTrace
            commonFunctions.showToast(requireContext(), e.message)

        } catch (e: SocketTimeoutException) {
            e.message
            commonFunctions.showToast(requireContext(), e.message)
        }//////////End try
    }

    ////////////////////////////UI Updates after save user successfull////////////
    private fun afterResult(response: ApolloResponse<UpdateUserMutation.Data>) {
        progressBar.visibility = View.GONE
        btnSave.visibility = View.GONE
        btnEdit.visibility = View.VISIBLE
        editGender.isEnabled = false
        editBirthday.isEnabled = false
        editAddress.isEnabled = false
        editName.isEnabled = false
        commonFunctions.showErrorSnackBar(
            requireContext(), progressBar, response.data?.updateUser?.message.toString(), false
        )

    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    class UploadImageTask(context: Context, bitmap: Bitmap) : AsyncTask<String?, Void?, String>() {
        var con = context
        var mBitMap = bitmap
        var commonFunctions = CommonFunctions(context)

        private fun getByteArray(inContext: Context, inImage: Bitmap): ByteArray? {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)

            return bytes.toByteArray()
        }

        private fun getRealPathFromURI(uri: Uri): String? {
            val cursor: Cursor? = con.contentResolver.query(uri, null, null, null, null)
            cursor?.moveToFirst()
            val index: Int? = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)

            return cursor?.getString(index!!)
        }

        override fun doInBackground(vararg params: String?): String? {
            /*try {
                val sourceFileUri = getImageUri(con, mBitMap)
                var conn: HttpURLConnection? = null
                var dos: DataOutputStream? = null
                val lineEnd = "\r\n"
                val twoHyphens = "--"
                val boundary = "*****"
                var bytesRead: Int
                var bytesAvailable: Int
                var bufferSize: Int
                val buffer: ByteArray
                val maxBufferSize = 1 * 1024 * 1024
                val sourceFile = File(getRealPathFromURI(sourceFileUri!!))
                //  sourceFile.mkdirs()
                if (sourceFile.isFile) {
                    try {
                        val upLoadServerUri = params[0]

                        // open a URL connection to the Servlet
                        val fileInputStream = FileInputStream(
                            sourceFile
                        )
                        val url = URL(upLoadServerUri)

                        // Open a HTTP connection to the URL
                        conn = url.openConnection() as HttpURLConnection
                        conn.doInput = true // Allow Inputs
                        conn.doOutput = true // Allow Outputs
                        conn.useCaches = false // Don't use a Cached Copy
                        conn.requestMethod = "PUT"

                        conn.setRequestProperty(
                            *//* "Content-Type",
                             "multipart/form-data;boundary=$boundary"*//*
                            "Content-Type", "image/jpeg"
                        )
                        dos = DataOutputStream(conn.outputStream)
                        dos.writeBytes(twoHyphens + boundary + lineEnd)
                        dos.writeBytes(
                            "Content-Disposition: form-data; name=\"bill\";filename=\"" + sourceFileUri + "\"" + lineEnd
                        )
                        dos.writeBytes(lineEnd)

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available()
                        bufferSize = Math.min(bytesAvailable, maxBufferSize)
                        buffer = ByteArray(bufferSize)

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize)
                            bytesAvailable = fileInputStream.available()
                            bufferSize = Math.min(bytesAvailable, maxBufferSize)
                            bytesRead = fileInputStream.read(
                                buffer, 0, bufferSize
                            )
                        }

                        // send multipart form data necesssary after file
                        // data...
                        dos.writeBytes(lineEnd)
                        dos.writeBytes(
                            (twoHyphens + boundary + twoHyphens + lineEnd)
                        )

                        // Responses from the server (code and message)
                        var serverResponseCode = conn.getResponseCode()
                        val serverResponseMessage: String = conn.responseMessage
                        if (serverResponseCode === 200) {

                            // messageText.setText(msg);
                            //Toast.makeText(ctx, "File Upload Complete.",
                            //      Toast.LENGTH_SHORT).show();

                            // recursiveDelete(mDirectory1);
                        }

                        // close the streams //
                        fileInputStream.close()
                        dos.flush()
                        dos.close()
                    } catch (e: java.lang.Exception) {

                        // dialog.dismiss();
                        e.printStackTrace()
                    }
                    // dialog.dismiss();
                } // End else block
            } catch (ex: java.lang.Exception) {
                // dialog.dismiss();
                ex.printStackTrace()
            }*/

            val client = OkHttpClient().newBuilder()
                .build()
            val mediaType: MediaType? = "image/jpeg".toMediaTypeOrNull()
            val body: RequestBody? = getByteArray(con, mBitMap)?.let { create(mediaType, it) }
            val request: Request? = params[0]?.let {
                Request.Builder()
                    .url(it)
                    .method("PUT", body)
                    .addHeader("Content-Type", "image/jpeg")
                    .build()
            }
            val response: Response? = request?.let { client.newCall(it).execute() }
            return response?.message + " " + response?.code.toString()
        }

        override fun onPostExecute(result: String) {
            commonFunctions.showToast(
                con,
                result

            )
        }

        override fun onPreExecute() {}
        override fun onProgressUpdate(vararg values: Void?) {}

    }


    private fun getAllBrowsers(): List<ResolveInfo> {
        val allLaunchers = ArrayList<String>()
        val myApps = Intent(Intent.ACTION_VIEW)
        myApps.data = Uri.parse("http://www.google.es")
        val myAppList: MutableList<ResolveInfo> =
            requireContext().packageManager.queryIntentActivities(myApps, PackageManager.MATCH_ALL)
        for (i in myAppList.indices) {
            if (myAppList[i].activityInfo.packageName == requireContext().packageName) {
//                Log.e("match", myAppList[i].activityInfo.packageName + "")
                myAppList.removeAt(i)
            }
        }
        return myAppList
    }

    override fun onSettingChanged(browserName: String) {
        tvFavouriteBrowser.text = browserName
    }
}


