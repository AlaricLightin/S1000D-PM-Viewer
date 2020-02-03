import Vue from 'vue'
import Router from 'vue-router'
import PublicationList from "../components/publication/PublicationList";
import Content from "../components/publication/Content";

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: '/',
            name: 'PublicationList',
            component: PublicationList
        },
        {
            path: '/content/:id',
            name: 'Content',
            component: Content
        }
    ]
})