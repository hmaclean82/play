package hmac.play.screens

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hmac.play.R
import hmac.play.models.PostWithUsername
import kotlinx.android.synthetic.main.post_item.view.*

class PostsAdapter(private var posts: List<PostWithUsername>, private val itemSize: Int): RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view, itemSize)
    }

    fun update(posts: List<PostWithUsername>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val postWithUsername = posts[position]
        val view = holder.itemView

        view.body.text = postWithUsername.post.title
        postWithUsername.username?.also {
            view.footer.text = it
            view.footer.visibility = View.VISIBLE}
            ?: run { view.footer.visibility = View.GONE }
    }

    override fun getItemViewType(position: Int) = 0

    class PostViewHolder(itemView: View, size: Int) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.layoutParams.width = size
            itemView.layoutParams.height = size
        }
    }
}