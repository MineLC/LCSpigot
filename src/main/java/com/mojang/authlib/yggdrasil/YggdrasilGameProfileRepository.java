package com.mojang.authlib.yggdrasil;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.tinylog.Logger;

public class YggdrasilGameProfileRepository implements GameProfileRepository {
   private static final String BASE_URL = "https://api.mojang.com/";
   private static final String SEARCH_PAGE_URL = "https://api.mojang.com/profiles/";
   private static final int ENTRIES_PER_PAGE = 2;
   private static final int MAX_FAIL_COUNT = 3;
   private static final int DELAY_BETWEEN_PAGES = 100;
   private static final int DELAY_BETWEEN_FAILURES = 750;
   private final YggdrasilAuthenticationService authenticationService;

   public YggdrasilGameProfileRepository(YggdrasilAuthenticationService authenticationService) {
      this.authenticationService = authenticationService;
   }

   public void findProfilesByNames(String[] names, Agent agent, ProfileLookupCallback callback) {
      Set<String> criteria = Sets.newHashSet();
      String[] arr$ = names;
      int len$ = names.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String name = arr$[i$];
         if (!Strings.isNullOrEmpty(name)) {
            criteria.add(name.toLowerCase());
         }
      }

      int page = 0;
      Iterator i$ = Iterables.partition(criteria, 2).iterator();

      while(i$.hasNext()) {
         List<String> request = (List)i$.next();
         int failCount = 0;

         while(true) {
            boolean failed = false;

            try {
               ProfileSearchResultsResponse response = (ProfileSearchResultsResponse)this.authenticationService.makeRequest(HttpAuthenticationService.constantURL("https://api.mojang.com/profiles/" + agent.getName().toLowerCase()), request, ProfileSearchResultsResponse.class);
               failCount = 0;
               Logger.debug("Page {} returned {} results, parsing", Integer.valueOf(page), response.getProfiles().length);
               Set<String> missing = Sets.newHashSet((Iterable)request);
               GameProfile[] dad = response.getProfiles();
               int leng = dad.length;

               for(int i = 0; i < leng; ++i) {
                  GameProfile profile = dad[i];
                  Logger.debug("Successfully looked up profile {}", profile);
                  missing.remove(profile.getName().toLowerCase());
                  callback.onProfileLookupSucceeded(profile);
               }

               Iterator sa = missing.iterator();

               while(sa.hasNext()) {
                  String name = (String)sa.next();
                  Logger.debug("Couldn't find profile {}", name);
                  callback.onProfileLookupFailed(new GameProfile((UUID)null, name), new ProfileNotFoundException("Server did not find the requested profile"));
               }

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var17) {
               }
            } catch (AuthenticationException var18) {
               AuthenticationException e = var18;
               ++failCount;
               if (failCount == 3) {
                  Iterator re = request.iterator();

                  while(re.hasNext()) {
                     String name = (String)re.next();
                     Logger.debug("Couldn't find profile {} because of a server error", name);
                     callback.onProfileLookupFailed(new GameProfile((UUID)null, name), e);
                  }
               } else {
                  try {
                     Thread.sleep(750L);
                  } catch (InterruptedException var16) {
                  }

                  failed = true;
               }
            }

            if (!failed) {
               break;
            }
         }
      }

   }
}
