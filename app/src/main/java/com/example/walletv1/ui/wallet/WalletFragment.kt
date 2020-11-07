package com.example.walletv1.ui.wallet

import android.app.Fragment.instantiate
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.Fragment.instantiate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.walletv1.R
import com.example.walletv1.net.RetrofitClientInstance

class WalletFragment : Fragment() {

    private lateinit var walletViewModel: WalletViewModel

    private lateinit var historyRecycler: RecyclerView
    private lateinit var sendButton: Button
    private lateinit var receiveButton: Button
    private lateinit var amountText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_wallet, container, false)
        walletViewModel =
            ViewModelProvider(this).get(WalletViewModel::class.java)
        val dataStore = root.context.createDataStore(name = "address_wallet")
        walletViewModel.readCounter(dataStore)

        sendButton = root.findViewById(R.id.send_button)
        sendButton.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorButAndItemWalletAtiva
            )
        )
        sendButton.setOnClickListener {
            val intent = Intent(context, SendActivity::class.java)
            startActivity(intent)
        }
        receiveButton = root.findViewById(R.id.receive_button)
        receiveButton.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorButAndItemWalletAtiva
            )
        )
        receiveButton.setOnClickListener {
            val dialogFragment = ReceiveDialogFragment()
            activity?.supportFragmentManager?.let { it1 ->
                dialogFragment.show(
                    it1,
                    "ReceiveDialogFragment"
                )
            }
        }
        amountText = root.findViewById(R.id.balance_text)

        //walletViewModel.getBalanceFromServer()
        walletViewModel.amount.observe(viewLifecycleOwner) {
            amountText.text = "${it.amountBGL}  BGL"
        }
        historyRecycler = root.findViewById(R.id.history_recycler)
        with(historyRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = walletViewModel.adapterRecyclerView
        }
        walletViewModel.getHistoryFromServer(context!!)

        return root
    }
}