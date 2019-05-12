package hmac.play.data

import hmac.play.models.PostWithComments
import hmac.play.models.raw.Comment
import hmac.play.models.raw.Post
import hmac.play.models.raw.User

object Social {

    val  posts: List<Post> = listOf(
        Post(1, 1, "Great Day!", "I wish every day was this good!"),
        Post(1, 2, "Rubbish Day", "I need a drink"),
        Post(2, 3, "Woah!", "That's a big one!"),
        Post(3, 4, "Purrrr", "Hey kitty")
    )

    val users: List<User> = listOf(
        User(1, "Bob", "Bobski24", "bobski24@gmail.com", null, null, null, null),
        User(2, "Charlie Brown", "brownwarrier34", "cbrown@babylon.com", null, null, null, null),
        User(3, "Suzie Jones", "bigsuze", "SuzieJones1990@hotmail.com", null, null, null, null),
        User(4, "Quiet Guy", "snoop", "anonymous@hotmail.com", null, null, null, null)
    )

    val comments: List<Comment> = listOf(
        Comment(1, 1, "Bob", "bobski24@gmail.com", "Nice one!"),
        Comment(2, 2, "Bob", "bobski24@gmail.com", "I'll join you!"),
        Comment(4, 3, "Charlie Brown", "cbrown@babylon.com", "Can I stroke it?"),
        Comment(4, 4, "Charlie Brown", "cbrown@babylon.com", "What's the name?")
    )

    val derivedPostsWithComments: List<PostWithComments> = listOf(
        PostWithComments("Bob", posts[0], listOf(comments[0])),
        PostWithComments("Bob", posts[1], listOf(comments[1])),
        PostWithComments("Charlie Brown", posts[2], emptyList()),
        PostWithComments("Suzie Jones", posts[3], listOf(comments[2], comments[3]))
    )


}