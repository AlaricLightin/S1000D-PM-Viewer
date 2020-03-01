<template>
    <custom-dialog v-if="!isCurrentUser(user.username)"
                   ref="main-dialog"
                   max-width="400px"
                   main-button-caption="Удалить"
                   form-caption="Удаление пользователя"
                   v-on:ok-button-click="deleteUser"
    >
        <template v-slot:alertText>Не удалось удалить пользователя.</template>
        <template v-slot:mainComponents><p>Вы действительно хотите удалить пользователя {{user.username}}?</p></template>
    </custom-dialog>
</template>

<script>
    import CustomDialog from "../customcomponents/CustomDialog";
    import {mapGetters} from "vuex";

    export default {
        name: "UserDelete",
        components: {CustomDialog},

        props: {
            user: Object
        },

        computed: {
            ...mapGetters('authentication', [
                'isCurrentUser'
            ])
        },

        methods: {
            deleteUser() {
                let mainDialog = this.$refs['main-dialog'];
                this.$store.dispatch('users/delete', this.user.username)
                    .catch(() => mainDialog.showAlert())
            },
        }
    }
</script>

<style scoped>

</style>