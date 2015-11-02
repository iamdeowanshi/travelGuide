package com.ithakatales.android.data.repository.realm;

import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.data.repository.AudioRepository;

/**
 * @author Farhan Ali
 */
public class AudioRepositoryRealm extends BaseRepositoryRealm<Audio> implements AudioRepository {

    public AudioRepositoryRealm() {
        super(Audio.class);
    }

}
