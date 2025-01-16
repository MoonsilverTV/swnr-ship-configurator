(ns moontv.swnr.shipconfigurator.events
  (:require [re-frame.core :as rf]
            [moontv.swnr.shipconfigurator.db-spec :as db-s]))

(rf/reg-event-db
 ::initialize
 (fn [_ _]                   ;; the two parameters are not important here, so use _
   (let [ship-data (->> {:strike-fighter ["Strike Fighter" 200000 5 5 8 1 1 16 5 2 1 ::db-s/fighter]
                         :shuttle ["Shuttle" 200000 3 0 15 1 10 11 3 5 1 ::db-s/fighter]
                         :free-merchant ["Free Merchant" 500000 3 2 20 1 6 14 10 15 2 ::db-s/frigate]
                         :patrol-boat ["Patrol Boat" 2500000 4 5 25 5 20 14 15 10 4 ::db-s/frigate]
                         :corvette ["Corvette" 4000000 2 10 40 10 40 13 15 15 6 ::db-s/frigate]
                         :heavy-frigate ["Heavy Frigate" 7000000 1 10 50 30 120 15 25 20 8 ::db-s/frigate]
                         :bulk-freighter ["Bulk Freighter" 5000000 0 0 40 10 40 11 15 25 2 ::db-s/cruiser]
                         :fleet-cruiser ["Fleet Cruiser" 10000000 1 15 60 50 200 14 50 30 10 ::db-s/cruiser]
                         :battleship ["Battleship" 50000000 0 20 100 200 1000 16 75 50 15 ::db-s/capital]
                         :small-station ["Small Station" 5000000 nil 5 120 20 200 11 50 40 10 ::db-s/cruiser]
                         :large-station ["Large Station" 40000000 nil 20 120 100 1000 17 125 75 30 ::db-s/capital]}
                        (map (fn [[key value]]
                               [key
                                (zipmap [::db-s/ship-name ::db-s/ship-cost ::db-s/ship-speed ::db-s/ship-armor
                                         ::db-s/ship-hp ::db-s/ship-crew-min ::db-s/ship-crew-max ::db-s/ship-ac
                                         ::db-s/ship-power ::db-s/ship-mass ::db-s/ship-hardpoints ::db-s/ship-class]
                                        value)]))
                        (into {}))] ;; TODO: fix ordering in UI (maybe i don't even want a map and instead want a [[] [] []...])
     {::db-s/ship-data ship-data
      ::db-s/selected-ship :large-station})))  ;; so the application state will initially be a map with two keys

(rf/reg-event-db
 ::select-ship
 (fn [db [_event-name type]]
   (when (not (= (::db-s/selected-ship db) type))
     (when (js/confirm "Changing your hull type will delete your data, are you sure?") ;; REVIEW: is this impurity okay?
       ;; TODO: implement undo / redo so we no longer need that check
       (assoc db ::db-s/selected-ship type)))))

