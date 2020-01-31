import Vue from 'vue'
import Router from 'vue-router'
import Publications from "../components/publication/Publications";
import Content from "../components/publication/Content";

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: '/',
            name: 'Publications',
            component: Publications
        },
        {
            path: '/content/:id',
            name: 'Content',
            component: Content
        }
    ]
})