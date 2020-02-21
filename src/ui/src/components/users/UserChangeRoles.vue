<template>
    <custom-dialog v-if="!isCurrentUser(user.username)"
                   ref="main-dialog"
                   max-width="400px"
                   main-button-caption="Изменить роли"
                   form-caption="Изменение ролей"
    >
        <template v-slot:alertText>Не удалось изменить роли.</template>

        <template v-slot:mainComponents>
            <v-container>
                <v-checkbox v-model="authorities" label="Администратор" value="ADMIN"/>
                <v-checkbox v-model="authorities" label="Редактор" value="EDITOR"/>
            </v-container>
        </template>

        <template v-slot:mainButton>
            <v-btn v-bind:loading="loading"
                   @click="changeClick">Изменить</v-btn>
        </template>
    </custom-dialog>
</template>

<script>
    import CustomDialog from "../customcomponents/CustomDialog";
    import {mapGetters} from "vuex";

    export default {
        name: "UserChangeRoles",
        components: {CustomDialog},

        props: {
            user: Object
        },

        data() {
            return {
                authorities: this.user.authorities,
                loading: false,
            }
        },

        computed: {
            ...mapGetters('authentication', [
                'isCurrentUser'
            ])
        },

        methods: {
            changeClick() {
                this.loading = true;
                let mainDialog = this.$refs['main-dialog'];
                this.$store.dispatch('users/changeRoles', {
                    username: this.user.username,
                    authorities: this.authorities
                })
                    .then(() => {
                        mainDialog.closeDialog();
                    })
                    .catch(() => {
                        mainDialog.showAlert();
                    })
                    .finally(() => this.loading = false);
            },
        }
    }
</script>

<style scoped>

</style>