package pl.konarzewski.uboze.ui.main.fragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import pl.konarzewski.uboze.R
import pl.konarzewski.uboze.model.getDict
import pl.konarzewski.uboze.model.getMemonicWords1


class MemonicFragment : Fragment() {

    private lateinit var searchText: EditText
    private lateinit var searchButton: Button
    private lateinit var searchResult: TextView
    private lateinit var dics: List<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.memonic_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchText = view.findViewById(R.id.search_text)
        searchButton = view.findViewById(R.id.search_button)
        searchResult = view.findViewById(R.id.search_result)

        val dicsPl = getDict("pl_full_except_en_full", requireContext()!!.applicationContext).take(70000)
        val dicsEn = getDict("en_full", requireContext()!!.applicationContext).take(30000)
        val imionaMeskie = getDict("imiona_meskie", requireContext()!!.applicationContext)
        val imionaZenskie = getDict("imiona_zenskie", requireContext()!!.applicationContext)
        val properNames = getDict("proper_names", requireContext()!!.applicationContext)

        dics = dicsPl.plus(dicsEn).plus(imionaMeskie).plus(imionaZenskie).plus(properNames)

        searchButton.setOnClickListener {
            searchResult.text = getMemonicWords1(searchText.text.toString(), dics)
            searchText.text = "".toEditable()
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}
