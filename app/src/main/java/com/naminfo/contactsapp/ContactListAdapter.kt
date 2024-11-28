package com.naminfo.contactsapp


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.naminfo.contactsapp.databinding.ItemContactlistBinding

private const val TAG = "==>>ContactListAdapter"
class ContactListAdapter(private var listOfContacts: MutableList<Contacts>,private var clickListener: (Contacts,Int) -> Unit) :
    RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder>() {
    lateinit var binding: ItemContactlistBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListViewHolder {
        // Inflate the binding for the item layout
        binding = ItemContactlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ContactListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactListViewHolder, position: Int) {
       holder.bind(listOfContacts[position],position)
    }

    override fun getItemCount(): Int = listOfContacts.size
  

    inner class ContactListViewHolder(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contacts,pos:Int) {
            Log.d(TAG, "bind: contacts:$contact")
            binding.contactNameTV.text = contact.name
            binding.contactNumberTV.text = contact.phone
            binding.mailIdTV.text = contact.email
            binding.deleteContactIMV.setOnClickListener {
                clickListener(contact,pos)
            }

        }
    }

    fun notify(listOfUpdatedContacts:List<Contacts>){
        listOfContacts=listOfUpdatedContacts.toMutableList()
        notifyDataSetChanged()
    }

}