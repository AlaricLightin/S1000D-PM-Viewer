import Vue from 'vue'
import Router from 'vue-router'
import PublicationList from "../components/publication/PublicationList";
import Content from "../components/publication/Content";
import UserList from "../components/users/UserList";

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
        },

        {
            path: '/users',
            name: 'UserList',
            component: UserList
        }
    ]
})