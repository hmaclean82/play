package hmac.play.screens

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hmac.play.R
import hmac.play.models.PostWithComments
import kotlinx.android.synthetic.main.post_item.view.*
import java.lang.ref.WeakReference

class PostsAdapter(private var posts: List<PostWithComments>, private val itemSize: Int): RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    private var listener: WeakReference<PostClickedListener>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view, itemSize)
    }

    fun setListener(listener: PostClickedListener) {
        this.listener = WeakReference(listener)
    }

    fun update(posts: List<PostWithComments>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val postWithComments = posts[position]
        val view = holder.itemView

        view.body.text = postWithComments.post.title
        postWithComments.author?.also {
            view.footer.text = it
            view.footer.visibility = View.VISIBLE}
            ?: run { view.footer.visibility = View.GONE }

        view.setOnClickListener { listener?.get()?.postClicked(postWithComments) }
    }

    override fun getItemViewType(position: Int) = 0

    class PostViewHolder(itemView: View, size: Int) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.layoutParams.width = size
            itemView.layoutParams.height = size
        }
    }

    interface PostClickedListener {
        fun postClicked(post: PostWithComments)
    }
}