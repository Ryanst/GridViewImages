##GridViewImages

###用两种方式来实现加载网络图片，压缩图片后显示在GridView，并缓存在内存中

1.  AsyncTask加载网络图片，Bitmap.option设置压缩图片比例，显示在GridView中，实现AbsListView.OnScrollListener接口，重写onScroll和onScrollStateChanged实现动态加载图片，使用LruCache缓存图片
2.  使用Volley开源库
