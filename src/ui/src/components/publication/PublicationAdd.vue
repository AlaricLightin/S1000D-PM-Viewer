<template>
    <v-dialog v-if="isActive"
              v-model="dialog"
              persistent
              max-width="800px">
        <template v-slot:activator="{ on }">
            <v-btn v-bind:disabled="disabled" v-on="on">Добавить</v-btn>
        </template>
        <v-card>
            <v-card-title>
                <span class="headline">Добавить публикацию</span>
            </v-card-title>
            <v-card-text>
                <v-container>
                    <v-file-input label="Выберите XML-файл с публикацией в формате S1000D 4.1"
                                  accept=".xml"
                                  show-size
                                  @change="fileChanged"
                    />
                    <v-alert v-model="showErrorAlert"
                             type="error">
                        {{loadingErrorText}}
                    </v-alert>
                </v-container>
            </v-card-text>
            <v-card-actions>
                <v-spacer/>
                <v-btn v-bind:disabled="needDisableSubmitButton"
                       v-bind:loading="loading"
                       @click="submitFile()">Загрузить</v-btn>
                <v-btn @click="dialog = false">Отмена</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    // TODO написать ограничение по величине загружаемого файла
    import {mapGetters} from "vuex";

    export default {
        name: "PublicationAdd",

        props: {
            disabled: Boolean
        },

        data: () => ({
            dialog: false,
            file: null,
            loading: false,
            loadingErrorText: null,
        }),

        computed: {
            needDisableSubmitButton: function () {
                return !this.file || this.loading;
            },

            isActive: function () {
                return this.isEditor();
            },

            showErrorAlert: function () {
                return this.loadingErrorText !== null;
            }
        },

        methods: {
            fileChanged(file) {
                this.file = file;
            },

            submitFile() {
                this.loading = true;
                this.loadingErrorText = null;
                this.$store.dispatch('publications/add', this.file)
                    .then(() => {
                        this.loading = false;
                        this.dialog = false;
                    })
                    .catch((error) => {
                        this.loading = false;
                        if(error.response && error.response.data) {
                            // TODO Сделать более человекочитаемые результаты
                            // TODO предусмотреть, что тут может быть не 400, а 500
                            // TODO предусмотреть ошибку авторизации
                            this.loadingErrorText = 'Код ошибки: ' + error.response.data.errorCode;
                        }
                        else {
                            this.loadingErrorText = 'Нет связи с сервером';
                        }
                        this.showErrorAlert = true;
                    });
            },

            ...mapGetters('authentication', [
                'isEditor'
            ])
        },

        watch: {
            dialog: function(val, oldVal) {
                if (val && !oldVal) {
                    this.loadingErrorText = null;
                }
            }
        }
    }
</script>

<style scoped>

</style>