package top.ourfor.app.iplayx.page.search;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.ourfor.app.iplayx.model.EmbyMediaModel;
import top.ourfor.app.iplayx.store.GlobalStore;

@Slf4j
@Getter
@NoArgsConstructor
@HiltViewModel
public class SearchViewModel extends ViewModel {
    private final MutableLiveData<List<EmbyMediaModel>> suggestionItems = new MutableLiveData<>(new CopyOnWriteArrayList<>());
    private final MutableLiveData<List<EmbyMediaModel>> searchResult = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> keyword = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> isSearchTipVisible = new MutableLiveData<>(true);

    GlobalStore store;

    @Inject
    SearchViewModel(GlobalStore store) {
        this.store = store;
    }

    public void fetchSearchSuggestion(boolean force) {
        if (!force && !suggestionItems.getValue().isEmpty()) {
            suggestionItems.postValue(suggestionItems.getValue());
            return;
        }
        
        isLoading.postValue(true);
        store.searchSuggestion(items -> {
            isLoading.postValue(false);
            suggestionItems.postValue(items);
        });
    }

    public void fetchSearchSuggestion() {
        fetchSearchSuggestion(false);
    }

    public void search(String keyword) {
        if (keyword == null || keyword.isEmpty()) return;
        isLoading.postValue(true);
        store.search(keyword, result -> {
            isLoading.postValue(false);
            searchResult.postValue(result);
        });
    }
}
