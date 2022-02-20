(ns request-server-frontend.effects
  (:require 
   [re-frame.core :as re-frame]
   [reitit.frontend.easy :as rfe]
   ))

(re-frame/reg-fx
 ::navigate!
 (fn [k params query]
   (rfe/push-state k params query)))