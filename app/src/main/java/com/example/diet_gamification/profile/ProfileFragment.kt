package com.example.diet_gamifikasi.profile

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.diet_gamification.R
import com.example.diet_gamification.databinding.FragmentProfileBinding
import com.example.diet_gamification.model.AccountModel
import com.example.diet_gamification.profile.UserViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diet_gamification.shop.ShopAdapter
import com.example.diet_gamification.shop.ShopItem
import com.example.diet_gamification.shop.ShopRepository
import com.example.diet_gamifikasi.MainActivity
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

class ProfileFragment : Fragment() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var cardView: CardView
    private lateinit var edit: Button
    private lateinit var  logout: Button
    private lateinit var cvshop: CardView
    private lateinit var xp: TextView
    private lateinit var userViewModel: UserViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var name: TextView
    private var accountModel: AccountModel? = null
    private lateinit var username: TextView
    private lateinit var weightuser: TextView
    private lateinit var heightuser: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        auth = FirebaseAuth.getInstance()
        cardView = binding.Cardview
        edit = binding.edit
        xp = binding.tvExp
        name = binding.Name
        logout = binding.logout
        cvshop = binding.Cardviewshop
        username = binding.nameuser
        weightuser = binding.beratuser
        heightuser = binding.tinggiuser
        getAccountFromActivity()
//       getAccountFromBundle()  Nanti Aktifin klo udah bisa login pake firebase
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        setupDrawerNavigation()
//        observeUserData()
//        setupLogoutButton()
        edit.setOnClickListener {
            if(accountModel != null){
                showSettingDialog(requireContext(),accountModel,)
            }else{
                showLoginDialog(requireContext())
            }
        }
        cvshop.setOnClickListener {
            showShopDialog()
        }
        return binding.root
    }
    private fun showPickImageDialog(
        context: Context,
        accountModel: AccountModel?,
        onImageSelected: (String) -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.imagedialog, null)

        val image1 = view.findViewById<CircleImageView>(R.id.image1)
        val image2 = view.findViewById<CircleImageView>(R.id.image2)
        val image3 = view.findViewById<CircleImageView>(R.id.image3)

        val lock1 = view.findViewById<ImageView>(R.id.lock1)
        val lock2 = view.findViewById<ImageView>(R.id.lock2)
        val lock3 = view.findViewById<ImageView>(R.id.lock3)

        val unlocked = ShopRepository.getUnlockedItems(accountModel?.inventory)

        fun setupImage(id: String, image: CircleImageView, lock: ImageView, drawableName: String) {
            if (unlocked.contains(id)) {
                lock.visibility = View.GONE

                // 👉 Only call onImageSelected when the user clicks the image
                image.setOnClickListener {
                    onImageSelected(drawableName)
                }
            } else {
                lock.visibility = View.VISIBLE

                // Optional: prevent interaction with locked images
                image.setOnClickListener(null)

                // Optional: grey out locked images
                val matrix = ColorMatrix().apply { setSaturation(0f) }
                image.colorFilter = ColorMatrixColorFilter(matrix)
            }
        }

        setupImage("PP-1", image1, lock1, "isla")
        setupImage("PP-2", image2, lock2, "sigma")
        setupImage("PP-3", image3, lock3, "plank")

        AlertDialog.Builder(context)
            .setView(view)
            .setTitle("Choose Profile Picture")
            .setNegativeButton("Cancel", null)
            .show()
    }



    private fun showShopDialog() {
        // Inflate your “shop” dialog layout
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_shop, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerView)
        val btnClose    = dialogView.findViewById<Button>(R.id.btnCloseShop)

        // Build the parent AlertDialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // 1) Get your shop items
        val shopItems = ShopRepository.shopItems

        // 2) Wire up the adapter
        val adapter = ShopAdapter(shopItems,accountModel?.inventory) { item ->
            // Parse price
            val price = item.price.toIntOrNull() ?: 0
            // Read user XP from your accountModel
            val userXp = accountModel?.Exp ?: 0

            if (userXp < price) {
                // Not enough XP → show a warning
                AlertDialog.Builder(requireContext())
                    .setTitle("Not enough EXP")
                    .setMessage("You need $price EXP to buy “${item.nama}”, but you only have $userXp EXP.")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                // Enough XP → ask for confirmation
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Purchase")
                    .setMessage("Spend $price EXP to buy “${item.nama}”?")
                    .setPositiveButton("Buy") { _, _ ->
                        accountModel?.Exp = userXp - price
                        val currentInventory = accountModel?.inventory ?: ""
                        val itemId = item.id
                        val updatedInventory = if (currentInventory.isBlank()) {
                            itemId
                        } else {
                            "$currentInventory,$itemId"
                        }
                        accountModel?.inventory = updatedInventory

                        val mainActivity = activity as? MainActivity
                        mainActivity?.currentAccountModel = accountModel
                        mainActivity?.updateUsername()

                        val profileFragment = ProfileFragment()
                        openFragment(profileFragment)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter       = adapter

        // Close button just dismisses the shop dialog
        btnClose.setOnClickListener { dialog.dismiss() }

        // Optional: make the dialog’s background transparent
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    // Somewhere in your Fragment/Activity, update the on‑screen XP counter:
    private fun updateXpDisplay(newXp: Int) {
        accountModel?.Exp = newXp
    }
    private fun showSettingDialog(context: Context, accountModel: AccountModel?) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_settings, null)

        val etName = view.findViewById<TextInputEditText>(R.id.etName)
        val etWeight = view.findViewById<TextInputEditText>(R.id.etWeight)
        val etHeight = view.findViewById<TextInputEditText>(R.id.etHeight)
        val etFont = view.findViewById<AutoCompleteTextView>(R.id.etFont)
        val etTitle = view.findViewById<AutoCompleteTextView>(R.id.etTitle)
        val etGender = view.findViewById<AutoCompleteTextView>(R.id.etGender)
        val profile = view.findViewById<CircleImageView>(R.id.circleImageView)
        // Prefill existing data
        val genderOptions = listOf("Male", "Female")
        val genderAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, genderOptions)
        etGender.setAdapter(genderAdapter)
        etGender.setText(accountModel?.Gender, false)
        etName.setText(accountModel?.name)
        etWeight.setText(accountModel?.berat?.toString())
        etHeight.setText(accountModel?.tinggi?.toString())
//        etFont.setText(accountModel?.setting) // if storing font in setting
//        etTitle.setText(accountModel?.se) // if using Gender as a title input here
        val unlocked = ShopRepository.getUnlockedItems(accountModel?.inventory)

// Filter fonts from unlocked inventory
        val unlockedFonts = ShopRepository.shopItems
            .filter { it.id.startsWith("FT-") && unlocked.contains(it.id) }
            .map { it.nama }

// Filter titles from unlocked inventory
        val unlockedTitles = ShopRepository.shopItems
            .filter { it.id.startsWith("TL-") && unlocked.contains(it.id) }
            .map { it.nama }

// Apply font dropdown
        val fontAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, unlockedFonts)
        etFont.setAdapter(fontAdapter)

// Apply title dropdown
        val titleAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, unlockedTitles)
        etTitle.setAdapter(titleAdapter)
        val selectedFont = ShopRepository.shopItems.find { it.id == accountModel?.setting && it.id.startsWith("FT-") }?.nama
        val selectedTitle = ShopRepository.shopItems.find { it.id == accountModel?.Gender && it.id.startsWith("TL-") }?.nama

        etFont.setText(selectedFont ?: "", false)
        etTitle.setText(selectedTitle ?: "", false)
        var selectedProfile = accountModel?.setting?.takeIf { it.startsWith("PP-") }
        val imagePath = ShopRepository.shopItems.find { it.id == selectedProfile }?.dirimag
        imagePath?.let {
            val resId = context.resources.getIdentifier(it, "drawable", context.packageName)
            if (resId != 0) profile.setImageResource(resId)
        }
        profile.setOnClickListener {
            showPickImageDialog(context, accountModel) { selectedDrawableName ->
                val selectedItem = ShopRepository.shopItems.find { it.dirimag == selectedDrawableName && it.id.startsWith("PP-") }
                selectedItem?.let {
                    val resId = context.resources.getIdentifier(it.dirimag, "drawable", context.packageName)
                    if (resId != 0) profile.setImageResource(resId)
                    selectedProfile = it.id // save selected profile ID
                }
            }
        }
        AlertDialog.Builder(context)
            .setView(view)
            .setTitle("Edit Settings")
            .setPositiveButton("Save") { _, _ ->
                val selectedFontName = etFont.text.toString()
                val selectedFontId = ShopRepository.shopItems.find { it.nama == selectedFontName && it.id.startsWith("FT-") }?.id

                val selectedTitleName = etTitle.text.toString()
                val selectedTitleId = ShopRepository.shopItems.find { it.nama == selectedTitleName && it.id.startsWith("TL-") }?.id

                val allSelected = listOfNotNull(selectedProfile, selectedFontId, selectedTitleId)
                val newSetting = allSelected.joinToString(",")

                // Create or update the account model
                var accountModel = accountModel?.apply {
                    name = etName.text.toString()
                    berat = etWeight.text.toString().toIntOrNull() ?: berat
                    tinggi = etHeight.text.toString().toIntOrNull() ?: tinggi
                    Gender = etGender.text.toString()
                    setting = newSetting
                }

                val mainActivity = context as? MainActivity
                mainActivity?.currentAccountModel = accountModel
                mainActivity?.updateUsername()
                val profileFragment = ProfileFragment()
                openFragment(profileFragment)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRegisterDialog(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_register, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 👇 Get input fields

        val registerButton = dialogView.findViewById<Button>(R.id.btnRegister)
        val loginText = dialogView.findViewById<TextView>(R.id.tvLogin)
        val name = dialogView.findViewById<EditText>(R.id.etFullName)
        val email = dialogView.findViewById<EditText>(R.id.etEmail)
        val password = dialogView.findViewById<EditText>(R.id.etPassword)
        val checkpassword = dialogView.findViewById<EditText>(R.id.etConfirmPassword)
        val beratEditText = dialogView.findViewById<EditText>(R.id.etWeight)
        val genderDropdown = dialogView.findViewById<AutoCompleteTextView>(R.id.etGender)
        val tinggieditText = dialogView.findViewById<EditText>(R.id.etHeight)
        // 👇 Set up gender dropdown options
        val genderOptions = listOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, genderOptions)
        genderDropdown.setAdapter(adapter)

        registerButton.setOnClickListener {
            if (beratEditText.text.toString().trim().isEmpty() || genderDropdown.text.toString().trim().isEmpty()||
                email.text.toString().trim().isEmpty()|| password.text.toString().trim().isEmpty()||
                tinggieditText.text.toString().trim().isEmpty() || name.text.toString().trim().isEmpty()) {
                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }else if(password.text.toString() != checkpassword.text.toString()){
                Toast.makeText(context, "Password tidak sama", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(context, "Register clicked\nWeight: ${beratEditText.text.toString()} kg\nGender: ${genderDropdown.text.toString()}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        loginText.setOnClickListener {
            dialog.dismiss()
            showLoginDialog(context)
        }

        dialog.show()
    }


    private fun showLoginDialog(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_login, null)

        val emailEditText = dialogView.findViewById<EditText>(R.id.etEmail)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.etPassword)
        val loginButton = dialogView.findViewById<Button>(R.id.btnLogin)
        val registerText = dialogView.findViewById<TextView>(R.id.tvRegister)
        val forgotPasswordText = dialogView.findViewById<TextView>(R.id.tvForgotPassword)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            } else {
                if(email == "123" && password == "123"){
                    // TODO: Handle login logic here
                    Toast.makeText(context, "Logging in...", Toast.LENGTH_SHORT).show()
                    accountModel = AccountModel(
                        email     = "123",
                        name      = "Ambatukam",
                        Gender    = "male",        // <-- must be exactly `Gender`
                        Exp       = 500,           // <-- must be exactly `Exp`
                        berat     = 50,
                        tinggi = 170,
                        inventory = "FT-1"
                    )
                    val mainActivity = activity as? MainActivity
                    mainActivity?.currentAccountModel = accountModel
                    mainActivity?.updateUsername()
                    val profileFragment = ProfileFragment()
                    openFragment(profileFragment)
                }else{
                    Toast.makeText(context, "Email Atau Password Salah", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
        }

        registerText.setOnClickListener {
            Toast.makeText(context, "Navigate to Register", Toast.LENGTH_SHORT).show()
            showRegisterDialog(context)
            dialog.dismiss()
        }

        forgotPasswordText.setOnClickListener {
            Toast.makeText(context, "Handle Forgot Password", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }
    // Inside ProfileFragment
    private fun openFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null) // Optional, if you want to keep the back stack
        transaction.commit()
    }


    private fun showEditPasswordDialog() {
        val context = requireContext()

        // Create TextInputLayout
        val tilPassword = TextInputLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            isHintEnabled = true
            hint = "Password"
            setPadding(24, 24, 24, 0)
            setBoxBackgroundColorResource(android.R.color.transparent)
            endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
        }

        // Create TextInputEditText inside it
        val etPassword = TextInputEditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setHintTextColor(Color.parseColor("#AAAAAA"))
            setTextColor(Color.parseColor("#FFFFFF"))
            backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            setPadding(60, 40, 60, 40)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, 0, 0)
            compoundDrawablePadding = 20
            hint = "Password"
        }

        tilPassword.addView(etPassword)

        // Build AlertDialog
        AlertDialog.Builder(context)
            .setTitle("Edit Password")
            .setView(tilPassword)
            .setPositiveButton("Save") { _, _ ->
                val newPassword = etPassword.text.toString()
                if (newPassword.isNotEmpty()) {
                    // Do your password save logic here
                    Toast.makeText(context, "Password updated!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun showLoggedInState() {
        cardView.visibility=View.VISIBLE
        logout.visibility= View.VISIBLE
        name.setText(accountModel?.name)
        edit.text = "edit"
        xp.setText(accountModel?.Exp.toString() + " XP")
        weightuser.setText(accountModel?.berat.toString() + " KG")
        username.setText(accountModel?.name)
        heightuser.setText(accountModel?.tinggi.toString() + " CM")
        val mainActivity = activity as? MainActivity
        mainActivity?.applyFontIfAvailable(requireContext(), mainActivity.currentAccountModel?.setting, binding.root)
    }

    private fun showLoggedOutState() {
        cardView.visibility=View.GONE
        logout.visibility= View.GONE
        edit.text = "login"
        name.text = "Welcome Guest"
        logout.visibility=View.GONE
        xp.visibility=View.GONE
    }
    private fun getAccountFromActivity() {
        val mainActivity = activity as? MainActivity
        accountModel = mainActivity?.currentAccountModel

        if (accountModel != null) {
            Log.d("ProfileFragment2", "Received Account: ${accountModel!!.name}")
            showLoggedInState()

        } else {
            Log.e("ProfileFragment2", "AccountModel is null in MainActivity")
            showLoggedOutState()
        }
    }


    private fun setupDrawerNavigation() {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_login -> {
                    findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                }
                R.id.nav_bmi_calculator -> {
                    findNavController().navigate(R.id.action_profileFragment_to_bmiCalculatorFragment)
                }
                R.id.nav_shop -> {
                    findNavController().navigate(R.id.action_profileFragment_to_shopFragment)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun observeUserData() {
        userViewModel.username.observe(viewLifecycleOwner) { name ->
            binding.Name.text = name
        }

        userViewModel.exp.observe(viewLifecycleOwner) { exp ->
            binding.tvExp.text = "EXP: $exp"
        }
    }

    private fun setupLogoutButton() {
        binding.logout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
