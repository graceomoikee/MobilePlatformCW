package org.me.gcu.omoike_grace_s2125456.viewmodel;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.me.gcu.omoike_grace_s2125456.model.CurrencyItem;
import org.me.gcu.omoike_grace_s2125456.repository.CurrencyRepository;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CurrencyViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<CurrencyItem>> currencyData = new MutableLiveData<>();
    private final CurrencyRepository repository = new CurrencyRepository();

    public LiveData<ArrayList<CurrencyItem>> getCurrencyData() {
        return currencyData;
    }

    public void loadCurrencies() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ArrayList<CurrencyItem> data = repository.fetchAndParseData();
            currencyData.postValue(data);
        });
    }
}
