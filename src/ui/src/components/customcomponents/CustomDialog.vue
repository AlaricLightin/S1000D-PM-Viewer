<template>
    <v-dialog v-model="dialog"
              v-bind:max-width="maxWidth"
              persistent
    >
        <template v-slot:activator="{ on }">
            <v-btn v-on="on">{{mainButtonCaption}}</v-btn>
        </template>

        <v-card>
            <v-card-title class="headline">{{formCaption}}</v-card-title>
            <v-card-text>
                <v-container>
                    <v-alert v-if="showErrorAlert" type="error">
                        <slot name="alertText"/>
                    </v-alert>

                    <slot name="mainComponents"></slot>
                </v-container>
                <v-card-actions>
                    <v-spacer>
                        <slot name="mainButton">
                            <v-btn text @click="okButtonClick">{{mainButtonCaption}}</v-btn>
                        </slot>
                        <v-btn text @click="dialog = false">Отмена</v-btn>
                    </v-spacer>
                </v-card-actions>
            </v-card-text>
        </v-card>
    </v-dialog>
</template>

<script>
    export default {
        name: "custom-dialog",

        props: {
            maxWidth: String,
            mainButtonCaption: String,
            formCaption: String,
        },

        data() {
            return {
                dialog: false,
                showErrorAlert: false,
            }
        },

        methods: {
            okButtonClick: function () {
                this.showErrorAlert = false;
                this.$emit('ok-button-click');
            },

            closeDialog: function () {
                this.dialog = false;
            },

            showAlert: function () {
                this.showErrorAlert = true;
            }
        },

        watch: {
            dialog: function(val, oldVal) {
                if (val && !oldVal) {
                    this.showErrorAlert = false;
                    this.$emit('set-start-state');
                }
            }
        }
    }
</script>

<style scoped>

</style>