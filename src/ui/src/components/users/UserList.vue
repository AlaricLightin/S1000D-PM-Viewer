<template>
    <div>
        <v-progress-linear v-if="loading" indeterminate/>
        <loading-error-alert v-if="loadingError"/>

        <v-list>
            <v-list-item v-for="user in this.users" :key="user.username">
                <v-list-item-content>
                    <v-list-item-title>{{user.username}}</v-list-item-title>
                    <v-list-item-subtitle>{{getAuthorities(user)}}</v-list-item-subtitle>
                </v-list-item-content>

                <v-list-item-action>
                    <user-delete :user="user"/>
                </v-list-item-action>
            </v-list-item>
        </v-list>

        <user-add/>
    </div>
</template>

<script>
    import LoadingErrorAlert from "../errors/LoadingErrorAlert";
    import {mapState} from "vuex";
    import UserAdd from "./UserAdd";
    import UserDelete from "./UserDelete";

    export default {
        name: "UserList",
        components: {UserDelete, UserAdd, LoadingErrorAlert},

        computed: {
            ...mapState({
                users: state => state.users.all
            })
        },

        data: () => ({
            loadingError: false,
            loading: false
        }),

        mounted() {
            this.loading = true;
            this.$store.dispatch('users/load')
                .catch(() => this.loadingError = true)
                .finally(() => this.loading = false);
        },

        methods: {
            getAuthorities: function (user) {
                let resultArr = [];
                if(user["authorities"].includes('ADMIN'))
                    resultArr.push('Администратор');
                if(user["authorities"].includes('EDITOR'))
                    resultArr.push('Редактор');
                return resultArr.join(', ')
            }
        }

    }
</script>

<style scoped>

</style>