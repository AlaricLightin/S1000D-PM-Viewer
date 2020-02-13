<template>
    <custom-dialog v-if="isVisible"
                   ref="main-dialog"
                   max-width="400px"
                   main-button-caption="Удалить"
                   form-caption="Удаление пользователя"
                   v-on:ok-button-click="deleteUser"
    >
        <template v-slot:alertText>Не удалось удалить пользователя.</template>
        <template v-slot:mainComponents><p>Вы действительно хотите удалить пользователя?</p></template>
    </custom-dialog>
</template>

<script>
    import CustomDialog from "../customcomponents/CustomDialog";
    import {mapState} from "vuex";

    export default {
        name: "UserDelete",
        components: {CustomDialog},

        props: {
            user: Object
        },

        computed: {
            ...mapState({
                currentUser: state => state.authentication.user
            }),

            isVisible: function () {
                return this.user.username !== this.currentUser.username;
            }
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