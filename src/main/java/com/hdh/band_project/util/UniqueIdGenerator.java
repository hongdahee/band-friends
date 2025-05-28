package com.hdh.band_project.util;

import com.hdh.band_project.user.UserRepository;
import java.util.UUID;

public class UniqueIdGenerator {

    public static String generateUniqueId(UserRepository userRepository){
        String uniqueId;
        do {
            uniqueId = UUID.randomUUID().toString().replace("-", "");
        }while(userRepository.existsByUniqueId(uniqueId));
        return uniqueId.substring(0, 10);
    }

}
