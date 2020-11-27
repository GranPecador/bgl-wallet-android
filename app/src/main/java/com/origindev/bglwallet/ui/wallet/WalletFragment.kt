package com.origindev.bglwallet.ui.wallet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.origindev.bglwallet.R
import com.origindev.bglwallet.ui.wallet.dialogs.ReceiveDialogFragment

class WalletFragment : Fragment() {

    private lateinit var walletViewModel: WalletViewModel

    private lateinit var historyRecycler: RecyclerView
    private lateinit var sendButton: Button
    private lateinit var receiveButton: Button
    private lateinit var amountText: TextView
    private lateinit var amountUSDText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        walletViewModel =
            ViewModelProvider(this).get(WalletViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_wallet, container, false)

        val swipeRefreshLayout = root.findViewById<SwipeRefreshLayout>(R.id.wallet_refresh)
        swipeRefreshLayout.setOnRefreshListener {
            walletViewModel.getBalanceFromServer(context!!)
            walletViewModel.getHistoryFromServer(context!!)
            swipeRefreshLayout.isRefreshing = false
        }
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        sendButton = root.findViewById(R.id.send_button)
        sendButton.setOnClickListener {
            val intent = Intent(context, SendActivity::class.java)
            startActivity(intent)
        }
        receiveButton = root.findViewById(R.id.receive_button)
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
        amountUSDText = root.findViewById(R.id.balance_usd_text)
        walletViewModel.amount.observe(viewLifecycleOwner) {
            val bgl = it.amountBGL
            if (bgl != 0.0 && it.amountUSD == 0.0) {
                it.amountUSD = bgl * (walletViewModel.courseUsd.value ?: 0.0)
            }
            amountText.text = "${bgl}  BGL"
            amountUSDText.text = String.format("%.9f $", it.amountUSD)
        }
        walletViewModel.courseUsd.observe(viewLifecycleOwner) {
            val amountUsd = it * (walletViewModel.amount.value?.amountBGL ?: 0.0)
            walletViewModel.amount.value?.amountUSD = amountUsd
            amountUSDText.text = String.format("%.9f $", amountUsd)
        }
        historyRecycler = root.findViewById(R.id.history_recycler)
        with(historyRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = walletViewModel.adapterRecyclerView
            isNestedScrollingEnabled = false
        }

        return root
    }

    override fun onStart() {
        super.onStart()
        walletViewModel.getBalanceFromServer(this@WalletFragment.requireContext())
        walletViewModel.getHistoryFromServer(this@WalletFragment.requireContext())
    }
}