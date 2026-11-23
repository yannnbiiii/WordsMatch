package com.example.wordsmatch

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.wordsmatch.databinding.ActivityGameMenuBinding
import kotlinx.coroutines.delay
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameMenu : AppCompatActivity() {

    private lateinit var binding: ActivityGameMenuBinding
    private var kesempatanya: Int = 3
    private var sedangCek = false


    data class PasanganKata(
        val inggris: String,
        val indonesia: String
    )
    private lateinit var varnotifBenar: Animation
    // Bank kosakata

    private val pasanganEasy = listOf(
        PasanganKata("Table", "Meja"),
        PasanganKata("Apple", "Apel"),
        PasanganKata("Book", "Buku"),
        PasanganKata("Chair", "Kursi"),
        PasanganKata("Water", "Air"),
        PasanganKata("School", "Sekolah"),
        PasanganKata("Friend", "Teman"),
        PasanganKata("Window", "Jendela"),
        PasanganKata("Happy", "Bahagia"),
        PasanganKata("Learn", "Belajar")
    )

    private val pasanganHard = listOf(
        PasanganKata("Whatever", "Apapun"),
        PasanganKata("Stubborn", "Keras Kepala"),
        PasanganKata("Humble", "Rendah Hati"),
        PasanganKata("Clumsy", "Ceroboh"),
        PasanganKata("Fragile", "Rapuh"),
        PasanganKata("Vague", "Samar"),
        PasanganKata("Decent", "Layak"),
        PasanganKata("Grateful", "Bersyukur"),
        PasanganKata("Hesitate", "Ragu"),
        PasanganKata("Dread", "Takut")
    )
    private val pasanganCampuran = pasanganEasy + pasanganHard


    private var semuaPasangan = listOf<PasanganKata>()

    // Lima pasangan aktif
    private val pasanganAktif = mutableListOf<PasanganKata>()
    private val katasudahtertebak = mutableListOf<PasanganKata>()

    // Persediaan pasangan baru
    private val pasanganTersedia = mutableListOf<PasanganKata>()

    private var skor = 0
    private var nyawa = 3

    private var pilihanPertama: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val modePermainan = intent.getStringExtra("MODE") ?: "EASY"

        semuaPasangan = when (modePermainan) {
            "HARD" -> pasanganHard
            "CAMPURAN" -> pasanganCampuran
            else -> pasanganEasy
        }

        varnotifBenar = AnimationUtils.loadAnimation(this, R.anim.notifbenar)
        enableEdgeToEdge()

        binding = ActivityGameMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        updateSkor()
        updateNyawa()

        // Rekor diambil dari penyimpanan
        val rekor = getSharedPreferences("wordsmatch", MODE_PRIVATE)
            .getInt("rekor", 0)
        binding.rekorSaatIni.text = "Rekor: $rekor"

        mulaiPermainan()
        binding.refreshBtn.text = "Refresh $kesempatanya x"

        binding.refreshBtn.setOnClickListener {
            kesempatanya--
            if (kesempatanya != 0) {
                binding.refreshBtn.isEnabled
            } else {
                binding.refreshBtn.isEnabled = false
                binding.refreshBtn.setTextColor(getColor(R.color.white))
            }
            binding.refreshBtn.text = "Refresh $kesempatanya x"
            pilihanPertama?.alpha = 1f
            pilihanPertama = null

            pasanganTersedia.clear()
            pasanganTersedia.addAll(semuaPasangan.shuffled())
            pasanganAktif.clear()
            repeat(5) {
                if (pasanganTersedia.isNotEmpty()) {
                    pasanganAktif.add(pasanganTersedia.removeAt(0))
                }
            }
            tampilkanKata()
        }
    }

    private fun updateSkor() {
        binding.benarSaatIni.text =
            getString(R.string.benar_saat_ini, skor)
    }

    private fun updateNyawa() {
        binding.nyawa.text = "❤".repeat(nyawa)
    }

    // Memulai permainan
    private fun mulaiPermainan() {

        pasanganAktif.clear()
        pasanganTersedia.clear()
        katasudahtertebak.clear()
        pasanganTersedia.addAll(semuaPasangan.shuffled())

        repeat(5) {
            pasanganAktif.add(
                pasanganTersedia.removeAt(0)
            )
        }

        tampilkanKata()
    }

    // Mengacak kedua bahasa secara terpisah
    private fun tampilkanKata() {

        val daftarIndonesia = pasanganAktif
            .map { it.indonesia }
            .shuffled()

        val daftarInggris = pasanganAktif
            .map { it.inggris }
            .shuffled()

        buatTombol(daftarIndonesia, daftarInggris)
    }

    // Membuat tombol dua kolom
    private fun buatTombol(
        daftarIndonesia: List<String>,
        daftarInggris: List<String>
    ) {

        val div2btn = binding.div2btn
        div2btn.removeAllViews()

        val arcaFont =
            androidx.core.content.res.ResourcesCompat.getFont(
                this,
                R.font.arca_majora_3_bold
            )

        // Lebar tombol
        val lebarPx =
            (140 * resources.displayMetrics.density).toInt()

        for (i in 0 until 5) {

            val barisLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
            }

            fun buatSatuTombol(kata: String): Button { //: Button artinya harus mengembalikan nilai button

                return Button(this).apply {

                    text = kata
                    textSize = 18f

                    typeface = arcaFont // font familly

                    backgroundTintList =
                        getColorStateList(R.color.main_color)

                    updatePadding(0, 0, 0, 14)
                    setTextColor(Color.WHITE)

                    layoutParams = LinearLayout.LayoutParams(
                        lebarPx, // lebarPx adalah var dengan nilai 140dp sebelumnya
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    setOnClickListener {
                        pilihKata(this)
                    }
                }
            }

            // Kiri: Bahasa Indonesia
            val tombolIndonesia =
                buatSatuTombol(daftarIndonesia[i])

            // Kanan: Bahasa Inggris
            val tombolInggris =
                buatSatuTombol(daftarInggris[i])

            val spacer = android.widget.Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, 1).apply {
                    weight = 1f
                }
            }

            barisLayout.addView(tombolIndonesia)
            barisLayout.addView(spacer)
            barisLayout.addView(tombolInggris)

            div2btn.addView(barisLayout)
        }
    }

    // Memilih kata
    private fun pilihKata(tombol: Button) {
        if (sedangCek) return


        if (pilihanPertama == null) {
            pilihanPertama = tombol
            tombol.alpha = 0.5f

            return
        }

        if (pilihanPertama == tombol) {
            tombol.alpha = 1f
            pilihanPertama = null
            return
        }

        val kataPertama = pilihanPertama?.text.toString()
        val kataKedua = tombol.text.toString()

        periksaJawaban(kataPertama, kataKedua, tombol)
        lifecycleScope.launch {
            delay(1200)
        }
    }

    // Memeriksa pasangan kata
    private fun periksaJawaban(
        kata1: String,
        kata2: String,
        button2: Button
    ) {
        sedangCek = true
        // Urutan pilihan bebas: Indonesia dulu atau Inggris dulu
        val pasanganBenar = pasanganAktif.find {
            (it.indonesia == kata1 && it.inggris == kata2) ||
                    (it.inggris == kata1 && it.indonesia == kata2)
        }

        if (pasanganBenar != null) { // jika pasangan benar
            binding.notifBenar.text = "Benar! skor bertambah 1"
            binding.notifBenar.visibility = android.view.View.VISIBLE
            binding.notifBenar.startAnimation(varnotifBenar)

            // Jawaban benar
            skor++
            updateSkor()

            katasudahtertebak.add(pasanganBenar)

            if (katasudahtertebak.size > 3) {
                katasudahtertebak.removeAt(0)
            }
            pasanganAktif.remove(pasanganBenar)

            // Mengisi ulang persediaan
            if (pasanganTersedia.isEmpty()) {
                pasanganTersedia.addAll(
                    semuaPasangan.filter { kandidat ->
                        kandidat !in pasanganAktif &&
                                kandidat !in katasudahtertebak
                    }.shuffled()
                )
            }

            // Memasukkan pasangan baru
            if (pasanganTersedia.isNotEmpty()) {

                pasanganAktif.add(
                    pasanganTersedia.removeAt(0)
                )
            }

            pilihanPertama?.alpha = 1f
            pilihanPertama?.backgroundTintList = getColorStateList(R.color.hijau_benar)
            button2.backgroundTintList = getColorStateList(R.color.hijau_benar)
            lifecycleScope.launch {
                pilihanPertama = null
                delay(1200)
                binding.notifBenar.visibility = android.view.View.INVISIBLE
                tampilkanKata()
                sedangCek = false
            }

        } else {
            sedangCek = true


            // Jawaban salah
            binding.notifBenar.text = "Salah! nyawa berkurang 1"
            binding.notifBenar.visibility = android.view.View.VISIBLE
            binding.notifBenar.startAnimation(varnotifBenar)

            nyawa--
            updateNyawa()

            // isi ulang lagi
            if (pasanganTersedia.isEmpty()) {

                pasanganTersedia.addAll(
                    semuaPasangan.filter { kandidat ->
                        kandidat !in pasanganAktif &&
                                kandidat != pasanganBenar
                    }.shuffled()
                )
            }


            pilihanPertama?.alpha = 1f
            pilihanPertama?.backgroundTintList = getColorStateList(R.color.merah_salah)
            button2.backgroundTintList = getColorStateList(R.color.merah_salah)
            lifecycleScope.launch {
                pilihanPertama = null
                delay(1200)
                binding.notifBenar.visibility = android.view.View.INVISIBLE
                tampilkanKata()
                sedangCek = false
            }

            // Game Over
            if (nyawa <= 0) {

                // Simpan rekor kalau skor sekarang lebih tinggi
                val prefs = getSharedPreferences("wordsmatch", MODE_PRIVATE)
                val rekorLama = prefs.getInt("rekor", 0)
                val rekorBaru = maxOf(rekorLama, skor)
                prefs.edit().putInt("rekor", rekorBaru).apply()

                // Kirim ke server kalau sedang login
                Session.username(this)?.let { user ->
                    ApiClient.kirimSkor(user, rekorBaru)
                }

                val intent = Intent(
                    this,
                    GameOverOrder::class.java
                )

                val intent2 = Intent(
                    this,
                    MainActivity::class.java
                )

                intent.putExtra("skor", skor)
                intent.putExtra("rekor", rekorBaru)
                intent2.putExtra("rekor", rekorBaru)
                startActivity(intent)
                finish()
            }

        }
    }
}