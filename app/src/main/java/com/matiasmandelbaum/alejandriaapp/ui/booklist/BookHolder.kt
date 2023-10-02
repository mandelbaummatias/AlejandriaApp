import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.ui.booklist.Book

class BookHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val titleTextView: TextView = itemView.findViewById(R.id.bookTitle)
    private val authorTextView: TextView = itemView.findViewById(R.id.bookAuthor)
    private val ratingTextView: TextView = itemView.findViewById(R.id.bookCalification)
    private val coverImageView: ImageView = itemView.findViewById(R.id.imageView)

    fun bind(book : Book) {
        titleTextView.text = book.title
        authorTextView.text = book.author
        ratingTextView.text = book.rating?.toString()

        // Cargar la imagen utilizando Glide (u otra biblioteca de carga de imágenes)
        Glide.with(itemView.context)
            .load(book.imageUrl)
            .placeholder(R.drawable.ic_menu_book)
            .into(coverImageView)
    }
}