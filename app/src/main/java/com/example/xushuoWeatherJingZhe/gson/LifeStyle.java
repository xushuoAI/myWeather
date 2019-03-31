package com.example.xushuoWeatherJingZhe.gson;

public class LifeStyle {


       /*   "brf": "舒适",
                  "txt": "今天夜间不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。",
                  "type": "comf"
*/

        public String brf; //生活指数简介
        public String txt;//内容
        public String type;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }


}
