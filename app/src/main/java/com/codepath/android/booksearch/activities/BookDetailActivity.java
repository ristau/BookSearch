package com.codepath.android.booksearch.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.codepath.android.booksearch.R;
import com.codepath.android.booksearch.models.Book;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BookDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPubDate;
    private TextView mTitle;
    private ShareActionProvider mShareActionProvider;
    private Intent shareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Fetch views
        ImageView ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

        // Extract book object from intent extras
        Book selectedBook = (Book) getIntent().getParcelableExtra("book");

        // Use book object to populate data into views
        ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        tvPubDate = (TextView) findViewById(R.id.tvPubDate);

        tvTitle.setText(selectedBook.getTitle());
        tvAuthor.setText(selectedBook.getAuthor());
        tvPubDate.setText(selectedBook.getPubDate());
        mTitle.setText(selectedBook.getTitle());

//        Glide.with(this)
//                .load(Uri.parse(selectedBook.getCoverUrl()))
//                .placeholder(R.drawable.ic_nocover)
//                .into(ivBookCover);

        // clear image resource
        ivBookCover.setImageResource(0);

        Glide.with(BookDetailActivity.this).load(selectedBook.getCoverUrl()).listener(new RequestListener<String, GlideDrawable>() {

            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                Toast.makeText(BookDetailActivity.this, "failed to load image", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                prepareShareIntent(((GlideBitmapDrawable) resource).getBitmap());
                attachShareIntentAction();
                Toast.makeText(BookDetailActivity.this, "success in image loading", Toast.LENGTH_SHORT).show();
                return false;
            }
        }).into(ivBookCover);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        attachShareIntentAction();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    // Gets the image URI and setup the associated share intent to hook into the provider
    public void prepareShareIntent(Bitmap drawableImage) {
        // Fetch Bitmap Uri locally
        ImageView ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
        Uri bmpUri = getBitmapFromDrawable(drawableImage); // see previous remote images section
        // Construct share intent as described above based on bitmap
        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra("title", "BOOK TITLE");
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/*");
    }

    // Attaches the share intent to the share menu item provider
    // call to update the share intent
    public void attachShareIntentAction() {
        if (mShareActionProvider != null && shareIntent != null)
            mShareActionProvider.setShareIntent(shareIntent);
    }


    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getBitmapFromDrawable(Bitmap bmp) {
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
            return bmpUri;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

}
