(ns moontv.swnr.shipconfigurator.events
  (:require [re-frame.core :as rf]
            [clojure.spec.alpha :as s]
            [moontv.swnr.shipconfigurator.db-spec :as db-s]))

(defn check-and-throw ;; TODO refactor this to be optimized out in prod?
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (js/alert "app-db spec violation")
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

;; now we create an interceptor using `after`
(def check-spec-interceptor (rf/after (partial check-and-throw ::db-s/app-db)))

(rf/reg-event-db
 ::initialize
 [check-spec-interceptor]
 (fn [_db? _params?]
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
                        (into {}))
         fitting-data
         {::db-s/advanced-lab
          {::db-s/fitting-name "Advanced Lab"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 10000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? true}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 2 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Skill bonus for analysis and research"}

          ::db-s/advanced-nav-computer
          {::db-s/fitting-name "Advanced Nav Computer"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 10000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? true}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 0 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Adds +2 for traveling familiar spike courses"}

          ::db-s/amphibious-operation
          {::db-s/fitting-name "Amphibious operation"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 25000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/fighter
           ::db-s/fitting-effect-text "Can land and can operate under water"}
          ::db-s/armory
          {::db-s/fitting-name "Armory"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 10000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 0 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 0 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Weapons and armor for the crew"}
          ::db-s/atmospheric-configuration
          {::db-s/fitting-name "Atmospheric configuration"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 5000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 0 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/fighter
           ::db-s/fitting-effect-text "Can land: frigates and fighters only"} ;; FIXME: this implies a max-class
          ::db-s/auto-targeting-system
          {::db-s/fitting-name "Auto-targeting system"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 50000 ::db-s/fitting-cost-scales? false}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 0 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/fighter
           ::db-s/fitting-effect-text "Fires one weapon system without a gunner"} ;; TODO: would these be better as a reader literal?
          ::db-s/automation-support
          {::db-s/fitting-name "Automation support"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 10000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 2 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/fighter
           ::db-s/fitting-effect-text "Ship can use simple robots as crew"}
          ::db-s/boarding-tubes
          {::db-s/fitting-name "Boarding tubes"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 5000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 0 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Allows boarding of a hostile disabled ship"}
          ::db-s/cargo-lighter
          {::db-s/fitting-name "Cargo lighter"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 25000 ::db-s/fitting-cost-scales? false}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 0 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 2 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Orbit-to-surface cargo shuttle"}
          ::db-s/cargo-space
          {::db-s/fitting-name "Cargo space"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 0 ::db-s/fitting-cost-scales? false}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 0 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/fighter
           ::db-s/fitting-effect-text "Pressurized cargo space"}
          ::db-s/cold-sleep-pods
          {::db-s/fitting-name "Cold sleep pods"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 5000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Keeps occupants in stasis"}
          ::db-s/colony-core
          {::db-s/fitting-name "Colony core"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 100000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 4 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 2 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Ship can be deconstructed ino a colony base"}
          ::db-s/drill-course-regulator
          {::db-s/fitting-name "Drill course regulator"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 25000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? true}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Common drill routes become auto-success"}
          ::db-s/drive-2-upgrade
          {::db-s/fitting-name "Drive-2 upgrade"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 10000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? true}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/fighter
           ::db-s/fitting-effect-text "Upgrade a spike drive to drive-2 rating"}
          ::db-s/drive-3-upgrade
          {::db-s/fitting-name "Drive-3 upgrade"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 20000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 2 ::db-s/fitting-power-scales? true}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 2 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/fighter
           ::db-s/fitting-effect-text "Upgrade a spike drive to drive-3 rating"}
          ::db-s/drive-4-upgrade
          {::db-s/fitting-name "Drive-4 upgrade"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 40000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 2 ::db-s/fitting-power-scales? true}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 3 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Upgrade a spike drive to drive-4 rating"}
          ::db-s/drive-5-upgrade
          {::db-s/fitting-name "Drive-5 upgrade"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 100000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 3 ::db-s/fitting-power-scales? true}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 3 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Upgrade a spike drive to drive-5 rating"}
          ::db-s/drive-6-upgrade
          {::db-s/fitting-name "Drive-6 upgrade"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 500000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 3 ::db-s/fitting-power-scales? true}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 4 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/cruiser
           ::db-s/fitting-effect-text "Upgrade a spike drive to drive-6 rating"}
          ::db-s/drop-pod
          {::db-s/fitting-name "Drop pod"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 300000 ::db-s/fitting-cost-scales? false}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 0 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 2 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Stealthed landing pod for troops"}
          ::db-s/emissions-dampers
          {::db-s/fitting-name "Emissions dampers"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 25000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? true}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/fighter
           ::db-s/fitting-effect-text "Adds +2 to skill checks to avoid detection"}
          ::db-s/exodus-bay
          {::db-s/fitting-name "Exodus bay"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 50000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? true}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 2 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/cruiser
           ::db-s/fitting-effect-text "House vast numbers of cold sleep passengers"}
          ::db-s/extended-life-support
          {::db-s/fitting-name "Extended life support"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 5000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? true}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/fighter
           ::db-s/fitting-effect-text "Doubles maximum crew size"}
          ::db-s/extended-medbay
          {::db-s/fitting-name "Extended medbay"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 5000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Can provide medical care to more patients"}
          ::db-s/extended-stores
          {::db-s/fitting-name "Extended stores"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 2500 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 0 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/fighter
           ::db-s/fitting-effect-text "Maximum life support duration is doubled"}
          ::db-s/fuel-bunkers
          {::db-s/fitting-name "Fuel bunkers"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 2500 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 0 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/fighter
           ::db-s/fitting-effect-text "Adds fuel for one more drill between fuelings"}
          ::db-s/fuel-scoops
          {::db-s/fitting-name "Fuel scoops"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 5000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 2 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Ship can scoop fuel from gas giant or star"}
          ::db-s/hydroponic-production
          {::db-s/fitting-name "Hydroponic production"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 10000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? true}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 2 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/cruiser
           ::db-s/fitting-effect-text "Ship produces life support resources"}
          ::db-s/lifeboats
          {::db-s/fitting-name "Lifeboats"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 2500 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 0 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Emergency escape craft for a ship's crew"}
          ::db-s/luxury-cabins
          {::db-s/fitting-name "Luxury cabins"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 10000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "10% of the max crew get luxurious quarters"}
          ::db-s/mobile-extractor
          {::db-s/fitting-name "Mobile extractor"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 50000 ::db-s/fitting-cost-scales? false}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 2 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Space mining and refinery fittings"}
          ::db-s/mobile-factory
          {::db-s/fitting-name "Mobile factory"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 50000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 3 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 2 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/cruiser
           ::db-s/fitting-effect-text "Self-sustaining factory and repair facilities"}
          ::db-s/precognitive-nav-chamber
          {::db-s/fitting-name "Precognitive nav chamber"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 100000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 0 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Allows a precog to assist in navigation"}
          ::db-s/psionic-anchorpoint
          {::db-s/fitting-name "Psionic anchorpoint"
           ::db-s/fitting-cost {::db-s/fitting-cost-base :special ::db-s/fitting-cost-scales? false}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 3 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 0 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Focal point for allied psychics' powers"}
          ::db-s/sensor-mask
          {::db-s/fitting-name "Sensor mask"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 10000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? true}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 0 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "At long distances, disguise ship as another"}
          ::db-s/ship-bay-figher
          {::db-s/fitting-name "Ship bay/fighter"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 200000 ::db-s/fitting-cost-scales? false}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 0 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 2 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/cruiser
           ::db-s/fitting-effect-text "Carrier housing for a fighter"}
          ::db-s/ship-bay-frigate
          {::db-s/fitting-name "Ship bay/frigate"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 1000000 ::db-s/fitting-cost-scales? false}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 4 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/capital
           ::db-s/fitting-effect-text "Carriour housing for a frigate"}
          ::db-s/ships-locker
          {::db-s/fitting-name "Ship's locker"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 2000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 0 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 0 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "General equipment for the crew"}
          ::db-s/shiptender-mount
          {::db-s/fitting-name "Shiptender mount"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 25000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Allow another ship to hitch on a spike drive"}
          ::db-s/smugglers-hold
          {::db-s/fitting-name "Smuggler's hold"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 2500 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 0 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/fighter
           ::db-s/fitting-effect-text "Small amount of well-hidden cargo space"}
          ::db-s/survey-sensor-array
          {::db-s/fitting-name "Survey sensor array"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 5000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 2 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Improved planetary sensory array"}
          ::db-s/system-drive
          {::db-s/fitting-name "System drive"
           ::db-s/fitting-cost {::db-s/fitting-cost-base :special ::db-s/fitting-cost-scales? false}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base -1 ::db-s/fitting-power-scales? true}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base -2 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/fighter
           ::db-s/fitting-effect-text "Replace spike drive with small system drive"}
          ::db-s/teleportation-pads
          {::db-s/fitting-name "Teleportation pads"
           ::db-s/fitting-cost {::db-s/fitting-cost-base :special ::db-s/fitting-cost-scales? false}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Pretech teleportation to and from ship"}
          ::db-s/tractor-beams
          {::db-s/fitting-name "Tractor beams"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 10000 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 2 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? false}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Manipulate objects in space at a distance"}
          ::db-s/vehicle-transport-fittings
          {::db-s/fitting-name "Vehicle transport fittings"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 2500 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 0 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 1 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Halve tonnage space of carried vehicles"}
          ::db-s/workshop
          {::db-s/fitting-name "Workshop"
           ::db-s/fitting-cost {::db-s/fitting-cost-base 500 ::db-s/fitting-cost-scales? true}
           ::db-s/fitting-power {::db-s/fitting-power-cost-base 1 ::db-s/fitting-power-scales? false}
           ::db-s/fitting-mass {::db-s/fitting-mass-cost-base 0.5 ::db-s/fitting-mass-scales? true}
           ::db-s/fitting-min-class ::db-s/frigate
           ::db-s/fitting-effect-text "Automated tech workshops for maintenance"}
          ;;
          }
         ;;
] ;; TODO::db-s/ fix ordering in UI (maybe i don't even want a map and instead want a [[] [] []...])
     {::db-s/ship-data ship-data
      ::db-s/selected-ship :large-station
      ::db-s/fitting-data fitting-data})))

(rf/reg-event-db
 ::select-ship
 [check-spec-interceptor]
 (fn [db [_event-name type]]
   (when (not (= (::db-s/selected-ship db) type))
     (when (js/confirm "Changing your hull type will delete your data, are you sure?") ;; TODO: get rid of this impurity, it ruins testing
       ;; TODO: implement undo / redo so we no longer need that check
       (assoc db ::db-s/selected-ship type)))))

