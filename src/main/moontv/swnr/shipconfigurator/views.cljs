(ns moontv.swnr.shipconfigurator.views
  (:require
   [moontv.swnr.shipconfigurator.db-spec :as db-s]
   [moontv.swnr.shipconfigurator.events :as events]
   [moontv.swnr.shipconfigurator.subs :as subs]
   [re-frame.core :as rf]))

;; TODO: really understand props passing, subscriptions, and rerenders (especially with callbacks)

(defn hull-selector-line [[ship-type ship-data] on-mouse-down]
  ^{:key ship-type}
  [:tr {:on-mouse-down on-mouse-down}
   (for [column [::db-s/ship-name ::db-s/ship-cost ::db-s/ship-speed ;; TODO: the columns shouldn't be decoupled between this and the column names
                 ::db-s/ship-armor ::db-s/ship-hp ::db-s/ship-crew-min
                 ::db-s/ship-crew-max ::db-s/ship-ac ::db-s/ship-power
                 ::db-s/ship-mass ::db-s/ship-hardpoints ::db-s/ship-class]]
     ^{:key column} [:td (get ship-data column)])])

(defn hull-selector
  []
  (let [ships @(rf/subscribe [::subs/ship-data])
        select-ship (fn [type] (rf/dispatch [::events/select-ship type]))]
    [:details
     {:open true}
     [:table
      [:thead [:tr (->> '("Hull Type" "Cost" "Speed" "Armor" "HP" "Crew-Min" "Crew-Max" "AC" "Power" "Mass" "Hardpoints" "Class")
                        (map (fn [column-name] ^{:key column-name} [:th column-name])))]]
      [:tbody
       (for [[ship-type ship] ships]
         ^{:key ship-type} [hull-selector-line [ship-type ship] #(select-ship ship-type)])]]]))

(defn fitting-selector
  []
  [:div "this is the fitting selector"])

(defn defenses-selector
  []
  [:div "this is the defenses selector"])

(defn weaponry-selector
  []
  [:div "this is the weaponry selector"])

(defn starship-summary
  []
  (let [selected-ship @(rf/subscribe [::subs/selected-ship])]
    [:div "this is the starship summary"
     [:table
      [:thead>tr>th (::db-s/ship-name (selected-ship @(rf/subscribe [::subs/ship-data])))]]])) ;; TODO: get this value injected instead

(defn swnr-starship-configurator-app
  []
  [:div
   [:h1 "SWNr Starship Configurator"]
   [hull-selector]
   [fitting-selector]
   [defenses-selector]
   [weaponry-selector]
   [starship-summary]])

(defn ui
  []
  [:div
   [:h1 "ze website"]
   [swnr-starship-configurator-app]])

