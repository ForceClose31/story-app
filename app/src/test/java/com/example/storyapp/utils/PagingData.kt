package com.example.storyapp.utils
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.data.local.entity.StoryEntity
import kotlinx.coroutines.Dispatchers

suspend fun collectDataUsingDiffer(pagingData: PagingData<StoryEntity>): List<StoryEntity> {
    val differ = AsyncPagingDataDiffer(
        diffCallback = StoryEntityDiffCallback(),
        updateCallback = NoopListUpdateCallback(),
        mainDispatcher = Dispatchers.Main,
        workerDispatcher = Dispatchers.IO
    )
    differ.submitData(pagingData)
    return differ.snapshot().items
}

class StoryEntityDiffCallback : DiffUtil.ItemCallback<StoryEntity>() {
    override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean = oldItem == newItem
}

class NoopListUpdateCallback : ListUpdateCallback {
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
}
