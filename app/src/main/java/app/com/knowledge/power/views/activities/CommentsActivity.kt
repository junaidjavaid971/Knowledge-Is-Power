package app.com.knowledge.power.views.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.knowledge.power.MessageTypes
import app.com.knowledge.power.R
import app.com.knowledge.power.adapters.MessagesAdapter
import app.com.knowledge.power.databinding.ActivityCommentsBinding
import app.com.knowledge.power.models.Message

class CommentsActivity : AppCompatActivity() {

    lateinit var binding: ActivityCommentsBinding
    lateinit var adapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comments)
        manageClicks()
        bindMessagesRV()
    }

    private fun bindMessagesRV() {
        adapter = MessagesAdapter(prepareMessages(), this)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true

        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.adapter = adapter
        binding.recyclerview.smoothScrollToPosition(9)
    }

    private fun manageClicks() {
        binding.ivAttachment.setOnClickListener {
            if (binding.layoutAttachment.visibility == View.GONE) {
                binding.layoutAttachment.visibility = View.VISIBLE
            } else {
                binding.layoutAttachment.visibility = View.GONE
            }
        }

        binding.recyclerview.setOnClickListener {
            if (binding.layoutAttachment.visibility == View.VISIBLE) {
                binding.layoutAttachment.visibility = View.GONE
            }
        }
    }

    override fun onBackPressed() {
        if (binding.layoutAttachment.visibility == View.VISIBLE) {
            binding.layoutAttachment.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    private fun prepareMessages(): ArrayList<Message> {
        val messageList: ArrayList<Message> = ArrayList()
        messageList.add(
            Message(
                "",
                "",
                "This location has a unique kind of turn at the end of street, please take care!",
                "",
                "",
                MessageTypes.TEXT,
                ""
            )
        )
        messageList.add(
            Message(
                "",
                "",
                "Take a short route from the street no 5 in order to avoid this turn",
                "",
                "",
                MessageTypes.TEXT,
                ""
            )
        )
        messageList.add(
            Message(
                "",
                "",
                "Here's a picture of the road!",
                "",
                "",
                MessageTypes.IMAGE,
                "https://thumbs.dreamstime.com/b/turning-roads-rizal-philippines-83915430.jpg"
            )
        )
        messageList.add(
            Message(
                "",
                "",
                "Thank you, that's helpful!",
                "",
                "",
                MessageTypes.TEXT,
                ""
            )
        )

        return messageList
    }
}