<template>
    <custom-dialog v-if="!isCurrentUser(user.username)"
                   ref="main-dialog"
                   max-width="400px"
                   main-button-caption="Изменить пароль"
                   form-caption="Изменение пароля"
                   v-on:set-start-state="setStartState"
    >
        <template v-slot:alertText>Не удалось изменить пароль.</template>

        <template v-slot:mainComponents>
            <v-form ref="form" v-model="valid">
                <v-row>
                    <v-text-field v-model="password"
                                  label="Пароль"
                                  type="password"
                                  :rules="passwordRules"
                                  required/>
                </v-row>
                <v-row>
                    <v-text-field v-model="password2"
                                  label="Повторите пароль"
                                  type="password"
                                  :rules="password2rules"
                                  required/>
                </v-row>
            </v-form>
        </template>

        <template v-slot:mainButton>
            <v-btn v-bind:loading="loading"
                   @click="changeClick">Изменить</v-btn>
        </template>
    </custom-dialog>
</template>

<script>
    import CustomDialog from "../customcomponents/CustomDialog";
    import {validationPasswordRules} from "../../utils/ValidatorUtils";
    import {mapGetters} from "vuex";

    export default {
        name: "UserChangePassword",
        components: {CustomDialog},

        props: {
            user: Object
        },

        data() {
            return {
                valid: false,

                password: '',
                password2: '',

                loading: false,

                passwordRules: validationPasswordRules(),

                password2rules: [
                    v => v === this.password || 'Пароли не совпадают.'
                ],
            }
        },

        computed: {
            ...mapGetters('authentication', [
                'isCurrentUser'
            ])
        },

        methods: {
            changeClick() {
                if (!this.$refs["form"].validate())
                    return;

                this.loading = true;
                let mainDialog = this.$refs['main-dialog'];
                this.$store.dispatch('users/changePassword', {
                    username: this.user.username,
                    password: this.password
                })
                    .then(() => {
                        mainDialog.closeDialog();
                    })
                    .catch(() => {
                        mainDialog.showAlert();
                    })
                    .finally(() => this.loading = false);
            },

            setStartState: function () {
                if(this.$refs["form"])
                    this.$refs["form"].reset();
            },
        }
    }
</script>

<style scoped>

</style>