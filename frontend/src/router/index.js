import { createRouter, createWebHistory } from "vue-router";
// const Game = () => import("@/views/Game");
const routes = [
  {
    path: "/",
    name: "Home",
    component: () => import("@/views/Game"),
  },
  // {
  //   path: "/about",
  //   name: "About",
  //   component: () =>
  //     import(/* webpackChunkName: "about" */ "../views/About.vue"),
  // },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
