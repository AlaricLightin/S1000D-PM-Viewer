<template>
    <v-dialog v-if="isActive"
            v-model="dialog"
            max-width="300">
        <template v-slot:activator="{ on }">
            <v-btn v-on="on">Удалить</v-btn>
        </template>
        <v-card>
            <v-card-title class="headline">Удаление публикации</v-card-title>
            <v-card-text>
                <p>Вы действительно хотите удалить публикацию?</p>
                <v-alert v-model="showErrorAlert" type="error">
                    Не удалось удалить публикацию.
                </v-alert>
            </v-card-text>
            <v-card-actions>
                <v-spacer>
                    <v-btn text @click="deletePublication()">Удалить</v-btn>
                    <v-btn text @click="dialog = false">Отмена</v-btn>
                </v-spacer>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import {mapGetters} from "vuex";

    export default {
        name: "PublicationDelete",

        props: {
            publication: Object
        },

        computed: {
            isActive: function () {
                return this.isAdmin();
            }
        },

        data () {
            return {
                dialog: false,
                showErrorAlert: false,
            }
        },

        methods: {
            deletePublication() {
                this.showErrorAlert = false;
                this.$store.dispatch('publications/delete', this.publication.id)
                    .catch(() => this.showErrorAlert = true)
            },

            ...mapGetters('authentication', [
                'isAdmin'
            ]),

            watch: {
                dialog: function(val, oldVal) {
                    if (val && !oldVal) {
                        this.showErrorAlert = false;
                    }
                }
            }
        }
    }
</script>

<style scoped>

</style>