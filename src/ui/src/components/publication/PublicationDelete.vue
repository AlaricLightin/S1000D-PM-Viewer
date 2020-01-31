<template>
    <v-dialog v-model="dialog" max-width="300">
        <template v-slot:activator="{ on }">
            <v-btn v-on="on">Удалить</v-btn>
        </template>
        <v-card>
            <v-card-title class="headline">Удаление публикации</v-card-title>
            <v-card-text>Вы действительно хотите удалить публикацию?</v-card-text>
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
    export default {
        name: "PublicationDelete",

        props: {
            publication: Object
        },

        data () {
            return {
                dialog: false,
            }
        },

        methods: {
            deletePublication() {
                // TODO обработать возможные ошибки
                this.$store.dispatch('publications/delete', this.publication.id);
            }
        }
    }
</script>

<style scoped>

</style>