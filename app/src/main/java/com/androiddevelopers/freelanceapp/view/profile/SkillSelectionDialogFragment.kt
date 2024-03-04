package com.androiddevelopers.freelanceapp.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.SelectableSkillAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentSkillSelectionDialogBinding

class SkillSelectionDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentSkillSelectionDialogBinding
    private var adapter = SelectableSkillAdapter()
    private val selectedSkills =   mutableSetOf<String>()

    // Listener interface oluştur
    interface OnSkillSelectedListener {
        fun onSkillsSelected(selectedSkills: List<String>)
    }

    // Listener tanımla
    private var skillSelectedListener: OnSkillSelectedListener? = null

    // Listener'a erişimi sağlayan metod
    fun setOnSkillSelectedListener(listener: OnSkillSelectedListener) {
        skillSelectedListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSkillSelectionDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        // values klasöründeki arrays.xml dosyasından yetenekleri al
        val skillArray = resources.getStringArray(R.array.important_skills).toList()
        adapter.skillList = skillArray
        // RecyclerView için adapter oluştur ve yetenekleri ver

        // RecyclerView'a adapter'ı ve layout manager'ı bağla
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // RecyclerView üzerindeki herhangi bir öğeye tıklandığında
        adapter.setOnItemClickListener(object : SelectableSkillAdapter.OnItemClickListener {
            override fun onItemClick(skill: String) {
                selectedSkills.add(skill)
            }
        })
        binding.searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val normalizedQuery = query?.toLowerCase()
                    val filteredList = skillArray.filter { it.toLowerCase().contains(normalizedQuery ?: "") }
                    adapter.skillList = filteredList
                    adapter.notifyDataSetChanged()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Arama metni değiştiğinde çağrılır
                newText?.let {
                    val normalizedQuery = newText?.toLowerCase()
                    val filteredList = skillArray.filter { it.toLowerCase().contains(normalizedQuery ?: "") }
                    adapter.skillList = filteredList
                    adapter.notifyDataSetChanged()
                }
                return true
            }
        })


        // OK düğmesine basıldığında seçilen yetenekleri dinleyiciye bildir ve fragment'i kapat
        binding.btnOk.setOnClickListener {
            skillSelectedListener?.onSkillsSelected(selectedSkills.toList())
            dismiss()
        }

        return view
    }



    // Seçilen yetenekler listesini döndüren bir metot
    fun getSelectedSkills(): List<String> {
        return selectedSkills.toList()
    }
}
